/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.impl;

import db.DB;
import db.DbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.dao.DepartmentDao;
import model.entities.Department;

/**
 *
 * @author Invasor_zim
 */
public class DepartmentDaoJDBC implements DepartmentDao{
    
    private Connection conn;
    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
                    "INSERT INTO department " +
                   "(Name) " +
                   "VALUES (?)" ,PreparedStatement.RETURN_GENERATED_KEYS                     
            );
            ps.setString(1, obj.getName());
            
            int valueId = ps.executeUpdate();
            if (valueId > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            }else{
                throw new DbException("Algo de errado aconteçeu (Programador burro!!)");
            }
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
            DB.closePreparedStatement(ps);
        }
    }

    @Override
    public void update(Department obj) {
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
                         "UPDATE department "+
                          "SET Name= ? WHERE Id= ?"                      
            );
            ps.setString(1, obj.getName());
            ps.setInt(2, obj.getId());
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
            DB.closePreparedStatement(ps);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
                         "DELETE FROM department "+
                          "WHERE Id= ?"
                                  
            );
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
            DB.closePreparedStatement(ps);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement( "SELECT * FROM department WHERE Id= ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                return dep;
            }
            return null;
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
            DB.closePreparedStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement( "SELECT * FROM department");
            rs = ps.executeQuery();
            List<Department> list = new ArrayList<>();
            while(rs.next()){
                Department dep = instantiateDepartment(rs);
                list.add(dep);
            }    
            return list;
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
            DB.closePreparedStatement(ps);
            DB.closeResultSet(rs);
        }
    }
    
     private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
    }
    
}
