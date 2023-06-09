package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.repository.CompanyDb;
import propensi.pmosystem.repository.ProjectDb;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    ProjectDb projectDb;
    @Autowired
    CompanyDb companyDb;
    @Override
    public List<ProjectModel> findAll(){
        return projectDb.findAll();
    }
    @Override
    public ProjectModel findById(Long id){
        Optional<ProjectModel> project = projectDb.findById(id);
        if(project.isPresent())
            return project.get();
        else
            return null;
    }
    @Override
    public ProjectModel addProject(ProjectModel project){
        return projectDb.save(project);
    }
    @Override
    public List<ProjectModel> findAllByClient(Long company){
        return projectDb.findAllByClient(company);
    }
    @Override
    public List<ProjectModel> findAllByConsultant(Long consultant){
        return projectDb.findAllByConsultant(consultant);
    }
    @Override
    public ProjectModel updateProject(ProjectModel updatedProject){
        projectDb.save(updatedProject);
        return updatedProject;
    }
    @Override
    public CompanyModel checkCompanyId(Long companyId){
        return companyDb.findById(companyId).get();
    }
    @Override
    public boolean isNameUnique(String projectName, String companyName){
        List<ProjectModel> projects = projectDb.findAll();
        for (ProjectModel project : projects){
            if (project.getName().equals(projectName) && project.getCompany().getName().equals(companyName))
                return false;
        }
        return true;
    }

}
