package matveydidenko.pocketproof.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Insert
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Delete
    suspend fun delete(receipt: ReceiptEntity)

    @Query("SELECT * FROM receipts ORDER BY timestamp DESC")
    fun getAllReceiptsFlow(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts ORDER BY timestamp DESC")
    suspend fun getAllReceipts(): List<ReceiptEntity>
}
