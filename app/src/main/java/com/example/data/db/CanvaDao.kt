package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DesignProject
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowMessage
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CanvaDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserProfile(userId: String = "user_me"): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(user: UserProfile)

    @Query("SELECT * FROM design_projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<DesignProject>>

    @Query("SELECT * FROM design_projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: String): DesignProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProject(project: DesignProject)

    @Query("DELETE FROM design_projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    @Query("SELECT * FROM escrow_contracts ORDER BY createdAt DESC")
    fun getAllContracts(): Flow<List<EscrowContract>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContract(contract: EscrowContract)

    @Query("SELECT * FROM chat_messages WHERE contractId = :contractId ORDER BY timestamp ASC")
    fun getMessagesForContract(contractId: String): Flow<List<EscrowMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: EscrowMessage)
}
