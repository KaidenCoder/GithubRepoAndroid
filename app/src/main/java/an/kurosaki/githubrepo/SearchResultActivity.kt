package an.kurosaki.githubrepo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val searchTerm = intent.getStringExtra("searchTerm")
        val retriever = GithubRetriever()

        if (searchTerm != null) {
            val callback = object : Callback<GithubSearchResults> {
                override fun onFailure(call: Call<GithubSearchResults>, t: Throwable) {
                    println("Not Working")
                }

                override fun onResponse(
                    call: Call<GithubSearchResults>,
                    response: Response<GithubSearchResults>
                ) {
                    val searchResults = response.body()
                    if (searchResults != null) {
                        for (repo in searchResults.items) {
                            listRepos(searchResults.items)
                        }
                    }
                }

            }
            retriever.getGithub(callback, searchTerm)
        } else {
            val username = intent.getStringExtra("username")
            val callback = object : Callback<List<Repo>> {
                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                    println("IT DON'T WORK")
                }

                override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {

                    if (response?.code() == 404) {
                        println("User does not exist")
                        val theView = this@SearchResultActivity.findViewById<View>(R.id.theMainView)
                        Snackbar.make(theView, "User not found. Try Again", Snackbar.LENGTH_LONG).show()
                    } else {
                        val repos = response?.body()
                        //println("IT WORK")
                        if (repos != null) {
                            listRepos(repos)
                        }
                    }
                }

            }
            retriever.userRepos(callback, username)
        }
    }

    fun listRepos(repos: List<Repo>) {
        val listView = findViewById<ListView>(R.id.repoListView)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            val selectedRepo = repos?.get(i)
            // OPEN THE URL IN A BROWSER
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRepo?.html_url))
            startActivity(intent)
        }

        val adapter =
            RepoAdapter(this@SearchResultActivity, android.R.layout.simple_list_item_1, repos!!)
        listView.adapter = adapter
    }
}

class RepoAdapter(context: Context?, resource: Int, objects: List<Repo>?) : ArrayAdapter<Repo>(
    context!!, resource, objects!!
) {
    override fun getCount(): Int {
        return super.getCount()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val repoView = inflator.inflate(R.layout.repo_list_layout, parent, false)
        val textView = repoView.findViewById<TextView>(R.id.repoTextView)
        val imageView = repoView.findViewById<ImageView>(R.id.repoImageView)

        val repo = getItem(position)
        if (repo != null) {
            Picasso.get().load(Uri.parse(repo.owner.avatar_url)).into(imageView)
        }


        if (repo != null) {
            textView.text = repo.full_name
        }

        return repoView
    }
}