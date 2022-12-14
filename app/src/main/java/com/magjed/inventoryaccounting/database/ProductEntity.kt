package com.magjed.inventoryaccounting.database

import androidx.annotation.Keep
import androidx.room.*
import com.magjed.inventoryaccounting.TABLE_HW_ITEMS
import java.io.Serializable

/**
 * Represents a computer hardware item.
 *
 * @param type is a type of the hardware, i.e. monitor, power supply, mouse etc.
 * @param model is a specific model name.
 * @param location is where the item can be found. May be a cabinet, warehouse and so on.
 * @param amount is how many such items are available now.
 *
 * @author Alexander Dyachenko
 */
@Entity(tableName = TABLE_HW_ITEMS)
@Keep
data class ProductEntity(
  val type: String,
  val model: String,
  val manufacturer: String,
  val location: String,
  val amount: Int
) : Serializable {
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
}

/**
 * DAO for interacting with hardware items table.
 *
 * @author Alexander Dyachenko
 */
@Dao
interface ProductsDao {

  @Query("select * from $TABLE_HW_ITEMS order by id desc")
  suspend fun getProducts(): List<ProductEntity>

  @Query(
    "select * from $TABLE_HW_ITEMS where " +
      "(type = :type or :type is null) and" +
      "(model = :model or :model is null) and" +
      "(manufacturer = :manufacturer or :manufacturer is null) and" +
      "(location = :location or :location is null) and" +
      "(amount = :amount or :amount is null) order by id desc"
  )
  suspend fun filter(
    type: String?,
    model: String?,
    manufacturer: String?,
    location: String?,
    amount: Int?
  ): List<ProductEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addProduct(product: ProductEntity)

  @Delete
  suspend fun removeProduct(product: ProductEntity)

  @Query("delete from $TABLE_HW_ITEMS where id = :id")
  suspend fun removeProduct(id: Int)
}