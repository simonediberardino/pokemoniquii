package com.simonediberardino.pokmoniquii.activities.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.simonediberardino.pokmoniquii.R
import com.simonediberardino.pokmoniquii.databinding.FragmentPokedexBinding

abstract class PokemonListFragment : Fragment() {
    abstract var _binding: Any?

    protected open var etSearchBar: EditText? = null
        set(value) {
            field = value
            field?.doOnTextChanged { text, _, _, _ -> onTextUpdated(text.toString()) }
        }

    protected lateinit var linearLayout: LinearLayout
    protected lateinit var scrollView: ScrollView
    protected var savedViews: MutableList<View> = mutableListOf()

    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    abstract fun initializeFragment(inflater: LayoutInflater, container: ViewGroup?)

    protected fun onTextUpdated(text: String){
        val text = text.trim()
        for(view: View in savedViews){
            if(text.length <= 1) {
                view.visibility = View.VISIBLE
                continue
            }

            val pokemonName = view.findViewById<TextView>(R.id.pokemon_name_tv).text.toString()

            if(!pokemonName.contains(text, ignoreCase = true))
                view.visibility = View.GONE
            else view.visibility = View.VISIBLE
        }
    }
}