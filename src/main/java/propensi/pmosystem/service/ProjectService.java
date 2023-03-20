package propensi.pmosystem.service;

import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.ProjectUserModel;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<ProjectModel> findAll();
    ProjectModel findById(Long id);
    ProjectModel addProject(ProjectModel project);
    List<ProjectModel> findAllByClient(Long company);
    List<ProjectModel> findAllByConsultant(Long consultant);
    ProjectModel updateProject(ProjectModel updatedProject);
    CompanyModel checkCompanyId(Long companyId);
    boolean isNameUnique(String projectName, String companyName);

}
