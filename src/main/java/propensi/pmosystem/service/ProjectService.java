package propensi.pmosystem.service;

import propensi.pmosystem.model.ProjectModel;

import java.util.List;

public interface ProjectService {
    List<ProjectModel> findAll();
    ProjectModel addProject(ProjectModel project);
    List<ProjectModel> findAllByClient(Long company);
    List<ProjectModel> findAllByConsultant(Long consultant);
}
