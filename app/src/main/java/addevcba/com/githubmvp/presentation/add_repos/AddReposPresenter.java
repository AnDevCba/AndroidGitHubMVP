package addevcba.com.githubmvp.presentation.add_repos;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import addevcba.com.githubmvp.data.model.Repo;
import addevcba.com.githubmvp.data.net.GitHubApiClient;
import addevcba.com.githubmvp.data.repository.ReposCache;
import addevcba.com.githubmvp.data.repository.ReposCallback;
import addevcba.com.githubmvp.domain.interactor.Interactor;
import addevcba.com.githubmvp.domain.interactor.SearchReposByUsernameInteractor;
import addevcba.com.githubmvp.presentation.show_repos.model.RepoUI;
import addevcba.com.githubmvp.presentation.show_repos.model.StickyHeaderUI;
import addevcba.com.githubmvp.presentation.show_repos.view.ViewType;
import addevcba.com.githubmvp.data.DependencyProvider;

/**
 * Presenter that handles user actions from {@link AddReposFragment} view,
 * shows repos by a given user name and updates the view.
 * <p>
 * Notice that Presenter knows nothing about Android framework.
 *
 * @author lucas.nobile
 */
public class AddReposPresenter implements AddReposContract.Presenter, ReposCallback {

    private AddReposContract.View view;
    private ReposCache reposCache;
    private Interactor interactor;
    private TreeMap<String, List<Repo>> reposByUsername;

    public AddReposPresenter(AddReposContract.View view) {
        this.view = view;
        reposCache = DependencyProvider.provideReposCache();
    }

    @Override
    public void searchReposByUsername(final String username) {
        if (username.isEmpty()) {
            view.showEmptyUsernameError();
        } else {
            view.hideSoftKeyboard();
            view.showProgressBar(true);
            interactor = new SearchReposByUsernameInteractor(username, DependencyProvider.provideRepositoryFactory(reposCache), this);
            interactor.execute();
        }
    }

    @Override
    public void onResponse(TreeMap<String, List<Repo>> reposByUsername) {
        // TODO Check if repos are already cached using reposCache
        if (reposCache.isCached(reposByUsername.firstKey())) {
            view.showProgressBar(false);
            view.showReposAlreadySaved();
        } else {
            this.reposByUsername = reposByUsername;
            view.showProgressBar(false);
            List<ViewType> items = transformResponseToViewTypes(reposByUsername);
            view.showRepos(items);
        }
    }

    @Override
    public void onError(String error) {
        view.showProgressBar(false);
        view.showError(error);
    }

    @Override
    public void saveReposByUsername() {
        String username = reposByUsername.firstKey();
        reposCache.put(username, reposByUsername.get(username));
        view.goToShowReposScreen();
    }

    @Override
    public void goToGitHubUsernamePage(String username) {
        String url = GitHubApiClient.BASE_URL + username;
        view.showGitHubUsernamePage(url);
    }

    @Override
    public void goToGitHubRepoPage(String url) {
        view.showGitHubRepoPage(url);
    }

    @Override
    public TreeMap<String, List<Repo>> getReposByUsername() {
        return reposByUsername;
    }

    @Override
    public void restoreStateAndShowReposByUsername(TreeMap<String, List<Repo>> reposByUsername) {
        if (reposByUsername != null) {
            List<ViewType> items = transformResponseToViewTypes(reposByUsername);
            view.showRepos(items);
        }
    }

    private List<ViewType> transformResponseToViewTypes(TreeMap<String, List<Repo>> reposByUsername) {
        List<ViewType> items = new ArrayList<>();
        String username = reposByUsername.firstKey();
        items.add(new StickyHeaderUI(username));
        List<Repo> repos = reposByUsername.get(username);
        for (Repo repo : repos) {
            items.add(new RepoUI(repo.getId(), repo.getName(), repo.getUrl(), repo.getCreatedAt(), repo.getStars(), repo.getLanguage()));
        }
        return items;
    }
}