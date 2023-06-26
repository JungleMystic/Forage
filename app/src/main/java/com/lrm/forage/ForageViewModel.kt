package com.lrm.forage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lrm.forage.database.Item
import com.lrm.forage.database.ItemDao
import kotlinx.coroutines.launch

class ForageViewModel(private val itemDao: ItemDao) : ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getAllItems().asLiveData()

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItem(
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String?
    ): Item {
        return Item(
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
    }

    fun addNewItem(
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String?
    ) {
        val newItem = getNewItem(name, address, inSeason, notes)
        insertItem(newItem)
    }

    fun isEntryValid(
        name: String,
        address: String
    ): Boolean {
        return if (name.isBlank() || address.isBlank()) {
            false
        } else true
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    private fun getUpdatedItem(
        itemId: Int,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String?
    ): Item {
        return Item(
            id = itemId,
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
    }

    fun updateItem(
        itemId: Int,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String?
    ) {
        val updatedItem = getUpdatedItem(
            itemId,
            name,
            address,
            inSeason,
            notes
        )

        updateItem(updatedItem)
    }
}

class ForageViewModelFactory(
    private val itemDao: ItemDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForageViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}