package springframework.guru.repoSearchEngine.service.repoSearchService;
import org.springframework.stereotype.Service;
import springframework.guru.repoSearchEngine.dto.models.RepoSearchItem;
import springframework.guru.repoSearchEngine.dto.bitbucket.BitbucketRepoDto;
import springframework.guru.repoSearchEngine.dto.github.GithubItem;
import springframework.guru.repoSearchEngine.dto.github.GithubSearchDto;
import springframework.guru.repoSearchEngine.dto.gitlab.GitlabRepoDto;
import springframework.guru.repoSearchEngine.service.bitbucketApiService.BitbucketApiService;
import springframework.guru.repoSearchEngine.service.githubApiService.GithubApiService;
import springframework.guru.repoSearchEngine.service.gitlabApiService.GitlabApiService;
import springframework.guru.repoSearchEngine.service.googleApiService.GoogleApiService;
import java.util.ArrayList;
import java.util.Set;

@Service
public class RepoSearchServiceImpl implements RepoSearchService {

    private final int REPO_SIZE = 10;
    private final int MAX_GOOGLE_PAGE = 9;
    private GitlabApiService gitlabApiService;
    private GithubApiService githubAPIService;
    private BitbucketApiService bitbucketApiService;
    private GoogleApiService googleApiService;

    public RepoSearchServiceImpl(GithubApiService githubAPIService,
                                 GitlabApiService gitlabApiService,
                                 BitbucketApiService bitbucketApiService,
                                 GoogleApiService googleApiService) {
        this.githubAPIService = githubAPIService;
        this.gitlabApiService = gitlabApiService;
        this.bitbucketApiService = bitbucketApiService;
        this.googleApiService = googleApiService;
    }

    @Override
    public ArrayList<RepoSearchItem> searchRepo(String searchKey){
        try{
            ArrayList<RepoSearchItem> repos = new ArrayList<>();
            searchGithubRepo(repos, searchKey);
            searchGitlabRepo(repos, searchKey);
            searchBitbucketRepo(repos, searchKey);
            return repos;
        }
        catch (Exception ex){
            return null;
        }

    }

    @Override
    public void searchGithubRepo(ArrayList<RepoSearchItem> repos, String searchKey){
        try{
            GithubSearchDto githubResult = githubAPIService.searchGithubRepo(searchKey);
            GithubItem[] githubItems = githubResult.getItems();
            for (int i = 0; i < Math.min(githubItems.length, REPO_SIZE); i++){
                GithubItem githubItem= githubItems[i];
                repos.add(new RepoSearchItem(githubItem));
            }
        }
        catch (Exception ex){
            return;
        }
    }

    @Override
    public void searchGitlabRepo(ArrayList<RepoSearchItem> repos, String searchKey){
        try{
            Set<String> repo_links = googleApiService.searchRepoLinks("gitlab", searchKey, 1);
            if(repo_links == null)
                return;
            acquireSingalGitlabRepo(repos, repo_links);
        }
        catch (Exception ex){
            return;
        }

    }

    @Override
    public void acquireSingalGitlabRepo(ArrayList<RepoSearchItem> repos, Set<String> repo_links ){
        try{
            for(String link : repo_links){
                GitlabRepoDto gitlabRepoDto = gitlabApiService.acquireSingleRepo(link);
                if (gitlabRepoDto ==null)
                    continue;
                repos.add( new RepoSearchItem(
                   ));
            }
        }
        catch(Exception ex){
            return;
        }
    }

    @Override
    public void searchBitbucketRepo(ArrayList<RepoSearchItem> repos, String searchKey){
        try{
            final int MAX_REPO_NUM = repos.size() + 10;
            for(int page = 0; page <= MAX_GOOGLE_PAGE; page++){
                if(repos.size() >= MAX_REPO_NUM)
                    return;
                Set<String> repo_fullnames = googleApiService.searchRepoLinks(
                        "bitbucket",
                        searchKey,
                        page*10+1
                );
                acquireSingalBitbucketRepo(repos, repo_fullnames, MAX_REPO_NUM);
            }
        }
        catch (Exception ex){
            return;
        }
    }

    @Override
    public void acquireSingalBitbucketRepo(ArrayList<RepoSearchItem> repos,
                                           Set<String> repo_fullnames,
                                           final int MAX_REPO_NUM){
        try{
            for(String repo_fullname : repo_fullnames){
                BitbucketRepoDto bitbucketRepoDto = bitbucketApiService.acquireSingleRepo(repo_fullname);
                if (bitbucketRepoDto == null)
                    continue;
                repos.add( new RepoSearchItem(bitbucketRepoDto));
                if(repos.size() >= MAX_REPO_NUM)
                    break;
            }
        }
        catch(Exception ex){
            throw ex;
        }
    }
}
