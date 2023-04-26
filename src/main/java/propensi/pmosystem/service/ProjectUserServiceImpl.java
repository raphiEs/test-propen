package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.ProjectUserModel;
import propensi.pmosystem.model.UserModel;
import propensi.pmosystem.repository.ProjectUserDb;

import java.util.List;

@Service
@Transactional
public class ProjectUserServiceImpl implements ProjectUserService{
    @Autowired
    ProjectUserDb projectUserDb;
    @Override
    public List<ProjectUserModel> findAllByUser(Long userId) {
        return projectUserDb.findAllByUserId(userId);
    }

    @Override
    public List<ProjectUserModel> findAll() {
        return projectUserDb.findAll();
    }

    @Override
    public ProjectUserModel addProjectUser(ProjectUserModel projectUser) {
        return projectUserDb.save(projectUser);
    }

    @Override
    public List<ProjectUserModel> findAllById(Long id){ return projectUserDb.findAllByProjectId(id); }

    @Override
    public void removeKonsultan(Long id, UserModel user){
        List<ProjectUserModel> list = projectUserDb.findAllByProjectId(id);
        for(ProjectUserModel x: list){
            if (x.getUser().equals(user)){
                projectUserDb.delete(x);
            }
        }
    }

    @Override
    public List<ProjectUserModel> findAllByProjectAndRole(Long idProject, Long role) {
        return projectUserDb.findAllByProjectIdAAndUserRole(idProject, role);
    }

    @Override
    public List<ProjectUserModel> findAllByProject(Long idProject) {
        return projectUserDb.findAllByProjectId(idProject);
    }

}