package service

import model.*
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery

class ProjectTaskService {

    suspend fun getAllProjectTasks(): List<ProjectTask> = dbQuery {
        TB_Project_task.selectAll().map { toProjectTask(it) }
    }

    suspend fun getProjectTask(id: Int): ProjectTask? = dbQuery {
        TB_Project_task.select {
            (TB_Project_task.id_task eq id)
        }.mapNotNull { toProjectTask(it) }
                .singleOrNull()
    }

    suspend fun updateProjectTask(ProjectTask: NewProjectTask): ProjectTask {
        val id = ProjectTask.id_task
        return if (id == null) {
            addProjectTask(ProjectTask)
        } else {
            dbQuery {
                TB_Project_task.update({ TB_Project_task.id_task eq id }) {
                    it[task_title] = ProjectTask.task_title
                    it[task_description] = ProjectTask.task_description
                    it[task_limit] = ProjectTask.task_limit
                    it[task_done] = ProjectTask.task_done
                    it[fk_project] = ProjectTask.fk_project
                }
            }
            getProjectTask(id)!!
        }
    }

    suspend fun addProjectTask(ProjectTask: NewProjectTask): ProjectTask {
        var key: Int? = 0
        dbQuery {
            key = TB_Project_task.insert({
                it[task_title] = ProjectTask.task_title
                it[task_description] = ProjectTask.task_description
                it[task_limit] = ProjectTask.task_limit
                it[task_done] = ProjectTask.task_done
                it[fk_project] = ProjectTask.fk_project
            }) get TB_Project_task.id_task
        }
        return getProjectTask(key!!)!!
    }

    suspend fun deleteProjectTask(id: Int): Boolean = dbQuery {
        TB_Project_task.deleteWhere { TB_Project_task.id_task eq id } > 0
    }

    private fun toProjectTask(row: ResultRow): ProjectTask =
            ProjectTask(
                    id_task = row[TB_Project_task.id_task],
                    task_title = row[TB_Project_task.task_title],
                    task_description = row[TB_Project_task.task_description],
                    task_limit = row[TB_Project_task.task_limit],
                    task_done = row[TB_Project_task.task_done],
                    fk_project = row[TB_Project_task.fk_project]
            )
}