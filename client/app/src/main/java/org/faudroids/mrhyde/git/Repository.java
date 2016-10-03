package org.faudroids.mrhyde.git;

import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.io.File;
import java.io.Serializable;

/**
 * A single locally available repository.
 */
public class Repository implements Serializable {

  private final String name;
  private final String cloneUrl;
  private final boolean isFavorite;
  private final AuthType authType;
  private final File rootDir;
  private final Optional<RepositoryOwner> owner;

  public Repository(
      @NonNull String name,
      @NonNull String cloneUrl,
      boolean isFavorite,
      @NonNull AuthType authType,
      @NonNull File rootDir,
      @NonNull Optional<RepositoryOwner> owner) {
    this.name = name;
    this.cloneUrl = cloneUrl;
    this.isFavorite = isFavorite;
    this.authType = authType;
    this.rootDir = rootDir;
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public String getCloneUrl() {
    return cloneUrl;
  }

  public Optional<RepositoryOwner> getOwner() {
    return owner;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public String getFullName() {
    if (!owner.isPresent()) {
      return name;
    }
    return String.format("%s/%s", owner.get().getUsername(), name);
  }

  public AuthType getAuthType() {
    return authType;
  }

  public File getRootDir() {
    return rootDir;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Repository)) return false;
    Repository that = (Repository) o;
    return isFavorite == that.isFavorite &&
        Objects.equal(name, that.name) &&
        Objects.equal(cloneUrl, that.cloneUrl) &&
        authType == that.authType &&
        Objects.equal(rootDir, that.rootDir) &&
        Objects.equal(owner, that.owner);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, cloneUrl, isFavorite, authType, rootDir, owner);
  }
}
