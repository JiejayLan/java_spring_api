package springframework.guru.repoSearchEngine.service.bitbucketApiService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import springframework.guru.repoSearchEngine.dto.bitbucket.BitbucketCommit;
import springframework.guru.repoSearchEngine.dto.bitbucket.BitbucketCommitsDto;
import springframework.guru.repoSearchEngine.dto.bitbucket.BitbucketRepoDto;
import java.net.URI;
import java.util.ArrayList;

@Service
public class BitbucketApiServiceImpl implements BitbucketApiService {
    private static String BITBUCKET_BASE_URL;

    public BitbucketApiServiceImpl(@Value("${BITBUCKET_BASE_URL}") String BITBUCKET_BASE_URL) {
        this.BITBUCKET_BASE_URL = BITBUCKET_BASE_URL;
    }

    public BitbucketRepoDto acquireSingleRepo(String path){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String request_url =  BITBUCKET_BASE_URL + path;
            BitbucketRepoDto bitbucketRepoDto = restTemplate.getForObject(request_url, BitbucketRepoDto.class);
            return bitbucketRepoDto;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public ArrayList<String> getRepoCommits(String path, int page){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String request_url =  BITBUCKET_BASE_URL + path + "/commits?page=" + page;
            BitbucketCommitsDto bitbucketCommitsDto = restTemplate.getForObject(request_url, BitbucketCommitsDto.class);
            ArrayList<String> dates = extractDateString(bitbucketCommitsDto);

            return dates;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public ArrayList<String> extractDateString(BitbucketCommitsDto bitbucketCommitsDto){
        BitbucketCommit[] bitbucketCommits =  bitbucketCommitsDto.getCommits();
        ArrayList<String> dates = new ArrayList<>();
        for(int i = 0; i < bitbucketCommits.length; i++){
            String date_str = bitbucketCommits[i].getDate();
            dates.add(date_str);
        }

        return dates;
    }
}
