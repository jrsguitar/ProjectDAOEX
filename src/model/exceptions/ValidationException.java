/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Invasor_zim
 */
public class ValidationException extends RuntimeException{
    private Map<String, String> erros = new HashMap<>();

    public ValidationException(String msg) {
        super(msg);
    }
      

    public Map<String, String> getErros() {
        return erros;
    }
    
    public void addError(String fildName, String errorMessage){
        erros.put(fildName, errorMessage);
    }
    
}
