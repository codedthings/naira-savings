package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdAtMillis DESC")
    fun getGoalsByUser(userId: String): Flow<List<GoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :goalId LIMIT 1")
    fun getGoalById(goalId: Long): Flow<GoalEntity?>

    @Query("SELECT * FROM savings_goals WHERE id = :goalId LIMIT 1")
    suspend fun getGoalByIdSync(goalId: Long): GoalEntity?

    @Query("SELECT * FROM savings_goals ORDER BY createdAtMillis DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Query("SELECT SUM(currentAmountNaira) FROM savings_goals WHERE userId = :userId")
    fun getTotalSavedByUser(userId: String): Flow<Double?>

    @Query("SELECT SUM(currentAmountNaira) FROM savings_goals")
    fun getTotalPlatformSavings(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM savings_goals")
    fun getTotalGoalsCount(): Flow<Int>
}
