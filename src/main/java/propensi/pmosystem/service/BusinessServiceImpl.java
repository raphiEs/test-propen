package propensi.pmosystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propensi.pmosystem.model.BusinessModel;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.repository.BusinessDb;
import propensi.pmosystem.repository.CompanyDb;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BusinessServiceImpl implements BusinessService{
    @Autowired
    BusinessDb businessDb;

    @Override
    public BusinessModel addBusiness(BusinessModel business) {
        businessDb.save(business);
        return business;
    }

    @Override
    public BusinessModel updateBusiness(BusinessModel business) {
        businessDb.save(business);
        return business;
    }

    @Override
    public BusinessModel deleteBusiness(BusinessModel business) {
        businessDb.delete(business);
        return business;
    }

    @Override
    public List<BusinessModel> getListBusiness() { return businessDb.findAll();
    }

    @Override
    public BusinessModel getBusinessById(Long id) {
        Optional<BusinessModel> business = businessDb.findById(id);
        if (business.isPresent()){
            return business.get();
        } else return null;
    }
}
