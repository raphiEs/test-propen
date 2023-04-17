package propensi.pmosystem.service;

import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.ProjectUserModel;
import propensi.pmosystem.model.UserModel;

import java.util.List;

public interface ProjectUserService {
    ProjectUserModel addProjectUser(ProjectUserModel projectUser);
    List<ProjectUserModel> findAllByUser(Long role);
    List<ProjectUserModel> findAll();
    List<ProjectUserModel> findAllById(Long id);
    void removeKonsultan(Long id, UserModel user);
}