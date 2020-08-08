package an.kurosaki.githubrepo

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("search/repositories?")
    fun searchRepos(@Query("q") searchTerm: String) : Call<GithubSearchResults>

    @GET("users/{user}/repos")
    fun userRepos(@Path("user") username: String) : Call<List<Repo>>
}

class GithubSearchResults(val items: List<Repo>)

class Repo(val full_name: String, val owner: GitHubUser, val html_url: String)
class GitHubUser(val avatar_url: String)

class GithubRetriever{
    val service: GithubService
    init {
        val retrofit = Retrofit.Builder().baseUrl("https://api.github.com/").addConverterFactory(GsonConverterFactory.create()).build()
        service = retrofit.create(GithubService::class.java)
    }

    fun getGithub(callback: Callback<GithubSearchResults>, searchTerm: String){
        var searchT = searchTerm
        if(searchT == ""){
            searchT = "News App"
        }
        val call =  service.searchRepos(searchT)
        call.enqueue(callback)
    }

    fun userRepos(callback: Callback<List<Repo>>, username: String){
        val call = service.userRepos(username)
        call.enqueue(callback)
    }

}