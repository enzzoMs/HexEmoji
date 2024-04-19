package sarueh.hexemoji.ui.fragments.play.adapters

import androidx.recyclerview.widget.RecyclerView
import sarueh.hexemoji.databinding.ItemHeaderPageDescriptionBinding

class PageDescriptionHolder(
    private val binding: ItemHeaderPageDescriptionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(description: String) {
        binding.pageDescription.text = description
    }
}