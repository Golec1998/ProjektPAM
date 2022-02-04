package com.example.projektpam.fragments.favourites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.projektpam.R
import com.example.projektpam.viewModel.EventsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.fragment_favourite.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavouriteFragment : Fragment() {

    private val args by navArgs<FavouriteFragmentArgs>()
    private lateinit var eventsViewModel : EventsViewModel
    val picasso = Picasso.get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        view.favouriteEventItemFavourite.setOnClickListener {
            updateFavouriteIcon(view, args.currentEvent.id)
        }
        view.backToFavouritesButton.setOnClickListener { findNavController().navigate(R.id.action_favouriteFragment_to_favouritesFragment) }
        view.favouriteEventLocationButton.setOnClickListener { showLocation() }
        view.favouriteEventName.text = args.currentEvent.name
        view.favouriteEventDescription.text = args.currentEvent.description
        picasso
            .load("https://beckertrans.pl/automobilevents_api/images/" + args.currentEvent.image + ".jpg")
            .into(view.favouriteEventImage)

        updateFavouriteIcon(view)

        return view
    }

    private fun updateFavouriteIcon(view : View, id : String = "x") {
        runBlocking {
            val pending = this.launch(Dispatchers.IO) {
                eventsViewModel.updateFavEvents(id)
                Thread.sleep(100)
            }
            pending.join()

            if(args.currentEvent.id.toInt() in eventsViewModel.favouriteEvents)
                view.favouriteEventItemFavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
            else
                view.favouriteEventItemFavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun showLocation()
    {
        val loc : String = "geo:" + args.currentEvent.coordinates
        val gmmIntentUri = Uri.parse(loc)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

}