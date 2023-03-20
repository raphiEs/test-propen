package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.ProjectUserModel;
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
}