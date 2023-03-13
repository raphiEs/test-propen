package propensi.pmosystem.service;

import propensi.pmosystem.model.ProjectModel;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<ProjectModel> findAll();
    ProjectModel findById(Long id);
    ProjectModel addProject(ProjectModel project);
    List<ProjectModel> findAllByClient(Long company);
    List<ProjectModel> findAllByConsultant(Long consultant);
    ProjectModel updateProject(ProjectModel updatedProject);
}
