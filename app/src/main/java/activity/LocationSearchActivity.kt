package activity

import adapter.SearchAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.km.deodeumi.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_location_search.*
import service.KakaoRestService




class LocationSearchActivity : AppCompatActivity() {
    private lateinit var subscription: Disposable
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        searchAdapter = SearchAdapter(this)
        search_recycler_view.adapter = searchAdapter

        val layoutManager = LinearLayoutManager(this)
        search_recycler_view.layoutManager = layoutManager
        search_recycler_view.setHasFixedSize(true)
        var dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(baseContext.getDrawable(R.drawable.recyclerview_divider))
        search_recycler_view.addItemDecoration(dividerItemDecoration)

        var intent = intent
        var address = intent.getStringExtra("myaddress")
        txt_my_location.text = "출발: $address"

        edit_search_destination.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.d("", "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("", "")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getKeywordSearch(edit_search_destination.text.toString())
            }
        })

        btn_close.setOnClickListener {
            edit_search_destination.text=Editable.Factory.getInstance().newEditable("")
        }
    }

    private fun getAddressSearch(keyword: String) {
        subscription = KakaoRestService.searchRestAPI().addressSearch(keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("addressResultKM", result.documents[0].toString())
                },
                { err ->
                    Log.e("Error User",err.toString())
                }
            )
    }

    private fun getKeywordSearch(keyword: String) {
        subscription = KakaoRestService.searchRestAPI().keywordSearch(keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.d("keywordResultKM", result.documents[0].place_name)

                    searchAdapter.removeAllItem()
                    searchAdapter.setSearchKeyword(keyword)
                    result.documents.forEach{
                        searchAdapter.addItem(it)
                    }
                },
                { err ->
                    Log.e("Error User",err.toString())
                }
            )
    }
}