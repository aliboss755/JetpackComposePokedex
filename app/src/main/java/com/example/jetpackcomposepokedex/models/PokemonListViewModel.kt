package com.example.jetpackcomposepokedex.models

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf

import androidx.palette.graphics.Palette
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepokedex.repository.PokemonRepository
import com.example.jetpackcomposepokedex.util.Constants
import com.example.jetpackcomposepokedex.util.Constants.PAGE_SIZE
import com.example.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
):ViewModel(){
    private var curPage = 0
    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    val loadError = mutableStateOf("")
    val isLoading =mutableStateOf(false)
    val endReached =mutableStateOf(false)
    fun loadPokemonPaginatrd(){
        viewModelScope.launch {
            isLoading.value =true
            val result =repository.getPokemonList(PAGE_SIZE,curPage * PAGE_SIZE)
            when(result){
                is  Resource.Success ->  {
                    endReached.value =curPage * PAGE_SIZE >=result.data!!.count
                    val pokedexEntrys =result.data.results.mapIndexed{ index , entry ->
                    val number =if (entry.url.endsWith("/")){
                        entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                    }else{
                        entry.url.takeLastWhile { it.isDigit() }
                    }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.capitalize(Locale.ROOT),url , number = number.toInt())
                    }
                    curPage++

                    loadError.value =""
                    isLoading.value =false
                    pokemonList.value +=pokedexEntrys
                }
                is  Resource.Error ->  {
                    loadError.value =result.message!!
                    isLoading.value=false
                }

                else -> {}
            }
        }
    }
    init {
        loadPokemonPaginatrd()
    }

    fun calcDominantColor(drawable:Drawable ,onFninish :(Color) -> Unit){
        val bmp =(drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)
        Palette.from(bmp).generate(){palitte ->
            palitte?.dominantSwatch?.rgb?.let { colorValue ->
                onFninish(Color(colorValue))
            }

        }
    }
}