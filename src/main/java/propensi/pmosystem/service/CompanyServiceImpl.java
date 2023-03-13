package propensi.pmosystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.repository.CompanyDb;

//import javax.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService{

    @Autowired
    CompanyDb companyDb;

    @Override
    public CompanyModel addCompany(CompanyModel company) {
        companyDb.save(company);
        return company;
    }

    @Override
    public CompanyModel updateCompany(CompanyModel company) {
        companyDb.save(company);
        return company;
    }

    @Override
    public CompanyModel deleteCompany(CompanyModel company) {
        companyDb.delete(company);
        return company;
    }

    @Override
    public List<CompanyModel> getListCompany() { return companyDb.findAll();
    }

    @Override
    public CompanyModel getCompanyById(Long id) {
        Optional<CompanyModel> company = companyDb.findById(id);
        if (company.isPresent()){
            return company.get();
        } else return null;
    }
}