package com.study.badrequest;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.*;

public interface DocsFileGenerators {

    static Attribute isRequired(){
        return key("Access").value("Required");
    }
}
