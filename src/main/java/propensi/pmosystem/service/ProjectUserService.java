package propensi.pmosystem.service;

import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.ProjectUserModel;

import java.util.List;

public interface ProjectUserService {
    ProjectUserModel addProjectUser(ProjectUserModel projectUser);
    List<ProjectUserModel> findAllByUser(Long role);
    List<ProjectUserModel> findAll();

}