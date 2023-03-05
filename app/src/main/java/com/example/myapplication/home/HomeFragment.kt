package com.example.myapplication.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DBKey.Companion.DB_ARTICLES
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDB: DatabaseReference

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            var articleModel = snapshot.getValue(ArticleModel::class.java)  //ArticleModel의 값들을 가지고 매핑을 진행
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }
    private var binding: FragmentHomeBinding? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //다른뷰왔다갔다할때 초기화를 해줘야함 아니면 계속 늘어남
        articleList.clear()
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        //DB를 가져옴
        articleDB = Firebase.database.reference.child(DB_ARTICLES)

        articleAdapter = ArticleAdapter()

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            context?.let{
                if(auth.currentUser!=null){
                    var intent = Intent(it,AddArticleActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Snackbar.make(view,"로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
                }

            }

        }

        //데이터를 가져오기 addChildListener , addListenerForSingleValueEvent(즉시성, 일회성)
        // Tab구조같은경우는 Fragments는 재사용됨 addChildEventListener는 중복될가능성이 있음
        // 그래서 켜질때는 켜지고 Fragment가 꺼질때는 Destroy되도록 함
        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }

}