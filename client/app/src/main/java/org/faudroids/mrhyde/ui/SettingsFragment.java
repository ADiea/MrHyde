package org.faudroids.mrhyde.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.faudroids.mrhyde.R;
import org.faudroids.mrhyde.app.MrHydeApp;
import org.faudroids.mrhyde.github.LoginManager;
import org.faudroids.mrhyde.ui.utils.AbstractFragment;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

public class SettingsFragment extends AbstractFragment {

  @BindView(R.id.versionTextView) protected TextView version;
  @BindView(R.id.authorTextView) protected TextView authors;
  @BindView(R.id.creditsTextView) protected TextView credits;
  @BindView(R.id.logoutTextView) protected TextView logout;

  @Inject
  LoginManager loginManager;


  public SettingsFragment() {
    super(R.layout.fragment_setting);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ((MrHydeApp) getActivity().getApplication()).getComponent().inject(this);

    version.setText(getVersion());

    setOnClickDialogForTextView(authors, R.string.about, R.string.about_msg);

    setOnClickDialogForTextView(credits, R.string.credits, R.string.credits_msg);

    logout.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        loginManager.clearAccount();
        getActivity().finish();
        startActivity(new Intent(getActivity(), LoginActivity.class));
      }
    });
  }

  private String getVersion() {
    try {
      return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException nnfe) {
      Timber.e(nnfe, "failed to get version");
      return null;
    }
  }

  private AlertDialog.Builder setOnClickDialogForTextView(TextView textView, final int titleResourceId, final int msgResourceId) {
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
        .setTitle(titleResourceId)
        .setMessage(Html.fromHtml("<font color='#000000'>" + getString(msgResourceId) + "</font>"))
        .setPositiveButton(android.R.string.ok, null);


    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog dialog = dialogBuilder.show();
        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
      }
    });


    return dialogBuilder;
  }

}
