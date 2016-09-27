package org.faudroids.mrhyde.ui.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.faudroids.mrhyde.R;
import org.faudroids.mrhyde.git.RepositoriesManager;
import org.faudroids.mrhyde.git.Repository;
import org.faudroids.mrhyde.github.GitHubManager;
import org.faudroids.mrhyde.ui.RepoOverviewActivity;
import org.faudroids.mrhyde.utils.DefaultErrorAction;
import org.faudroids.mrhyde.utils.DefaultTransformer;
import org.faudroids.mrhyde.utils.ErrorActionBuilder;
import org.faudroids.mrhyde.utils.HideSpinnerAction;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public abstract class AbstractReposFragment extends AbstractFragment {

  private static final int REQUEST_OVERVIEW = 41; // used to mark the end of an overview activity
  private static final DateFormat dateFormat = DateFormat.getDateInstance();

  @Inject protected GitHubManager gitHubManager;
  @Inject protected RepositoriesManager repositoriesManager;

  @BindView(R.id.list) protected RecyclerView recyclerView;
  protected RepositoryAdapter repoAdapter;


  public AbstractReposFragment() {
    this(R.layout.fragment_repos);
  }


  public AbstractReposFragment(int layoutResource) {
    super(layoutResource);
  }


  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // setup list
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);
    repoAdapter = new RepositoryAdapter();
    recyclerView.setAdapter(repoAdapter);
    loadRepositories();
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_OVERVIEW:
        loadRepositories();
        return;
    }
  }


  protected abstract void loadRepositories();


  protected void onRepositorySelected(Repository repository) {
    repositoriesManager
        .hasRepositoryBeenCloned(repository)
        .compose(new DefaultTransformer<>())
        .subscribe(
            hasBeenCloned -> {
              Intent repoIntent = new Intent(getActivity(), RepoOverviewActivity.class);
              repoIntent.putExtra(RepoOverviewActivity.EXTRA_REPOSITORY, repository);

              // repo has been clone, open it!
              if (hasBeenCloned) {
                startActivityForResult(repoIntent, REQUEST_OVERVIEW);
                return;
              }

              // check for pre v1 repos that can be imported
              if (repositoriesManager.canPreV1RepoBeImported(repository)) {
                new MaterialDialog
                    .Builder(getActivity())
                    .title(R.string.import_repo_title)
                    .content(R.string.import_repo_message)
                    .positiveText(R.string.import_repo_confirm)
                    .negativeText(android.R.string.cancel)
                    .checkBoxPromptRes(R.string.import_repo_check_import, true, null)
                    .onPositive((dialog, which) -> {
                      boolean importRepo = dialog.isPromptCheckBoxChecked();
                      cloneRepository(repoIntent, repository, importRepo);
                    })
                    .show();
                return;
              }

              // clone repo
              new MaterialDialog
                  .Builder(getActivity())
                  .title(R.string.clone_repo_title)
                  .content(R.string.clone_repo_message)
                  .positiveText(R.string.clone_repo_confirm)
                  .negativeText(android.R.string.cancel)
                  .onPositive((dialog, which) -> {
                    cloneRepository(repoIntent, repository, false);
                  })
                  .show();
            },
            new ErrorActionBuilder()
            .add(new DefaultErrorAction(getActivity(), "Failed to repo status"))
            .build()
    );

  }


  private void cloneRepository(Intent repoIntent, Repository repository, boolean importPreV1Repo) {
    showSpinner();
    repositoriesManager
        .cloneRepository(repository, importPreV1Repo)
        .compose(new DefaultTransformer<>())
        .subscribe(
            gitManager -> startActivityForResult(repoIntent, REQUEST_OVERVIEW),
            new ErrorActionBuilder()
                .add(new DefaultErrorAction(getActivity(), "Failed to repo status"))
                .add(new HideSpinnerAction(this))
                .build()
        );
  }


  protected final class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepoViewHolder> {

    private final List<Repository> repositoryList = new ArrayList<>();


    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repo, parent, false);
      return new RepoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RepoViewHolder holder, int position) {
      holder.setRepo(repositoryList.get(position));
    }


    @Override
    public int getItemCount() {
      return repositoryList.size();
    }


    public void setItems(Collection<Repository> repositoryList) {
      this.repositoryList.clear();
      this.repositoryList.addAll(repositoryList);
      Collections.sort(
          this.repositoryList,
          (lhs, rhs) -> lhs.getFullName().compareTo(rhs.getFullName())
      );
      notifyDataSetChanged();
    }


    public class RepoViewHolder extends RecyclerView.ViewHolder {

      private final View containerView;
      private final ImageView iconView;
      private final TextView titleView;
      private final ImageView heartView;

      public RepoViewHolder(View view) {
        super(view);
        this.containerView = view.findViewById(R.id.container);
        this.iconView = (ImageView) view.findViewById(R.id.icon);
        this.titleView = (TextView) view.findViewById(R.id.title);
        this.heartView = (ImageView) view.findViewById(R.id.heart);
      }

      public void setRepo(final Repository repo) {
        Picasso.with(getActivity())
            .load(repo.getOwner().get().getAvatarUrl().orNull())
            .resizeDimen(R.dimen.card_icon_size, R.dimen.card_icon_size)
            .placeholder(R.drawable.octocat_black)
            .transform(new CircleTransformation())
            .into(iconView);
        titleView.setText(repo.getFullName());
        containerView.setOnClickListener(v -> onRepositorySelected(repo));
        if (gitHubManager.isRepositoryFavourite(repo)) {
          heartView.setVisibility(View.VISIBLE);
          heartView.setOnClickListener(v -> new MaterialDialog.Builder(getActivity())
              .title(R.string.unmark_title)
              .content(R.string.unmark_message)
              .positiveText(android.R.string.ok)
              .onPositive((dialog, which) -> {
                gitHubManager.unmarkRepositoryAsFavourite(repo);
                loadRepositories();
              })
              .negativeText(android.R.string.cancel)
              .show());
        } else {
          heartView.setVisibility(View.GONE);
        }
      }
    }

  }

}
