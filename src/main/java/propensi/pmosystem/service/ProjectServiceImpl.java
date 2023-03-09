package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.repository.ProjectDb;

import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    ProjectDb projectDb;
    @Override
    public List<ProjectModel> findAll(){
        return projectDb.findAll();
    }
    @Override
    public ProjectModel addProject(ProjectModel project){
        return projectDb.save(project);
    }
    @Override
    public List<ProjectModel> findAllByClient(Long company){
        return projectDb.findAllByClient(company);
    }
    public List<ProjectModel> findAllByConsultant(Long consultant){
        return projectDb.findAllByConsultant(consultant);
    }

}
