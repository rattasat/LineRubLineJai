package com.me.linerublinejai.services;

import com.me.linerublinejai.models.entities.LineUser;
import com.me.linerublinejai.models.repositories.LineUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineUserService {

    @Autowired
    private LineUserRepository lineUserRepository;

    public LineUser getLineUser(String linseUserId) throws Exception{
        return lineUserRepository.findByLineUserId(linseUserId);
    }

}
