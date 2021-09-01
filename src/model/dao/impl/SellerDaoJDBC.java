/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.impl;

import db.DbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;
import db.DB;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Invasor_zim
 */
public class SellerDaoJDBC implements SellerDao {
    
    private Connection conn;
    
    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }
    
    
    
    @Override
    public void insert(Seller obj) {
        
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
                   "INSERT INTO seller " +
                   "(Name, Email, BirthDate, BaseSalary, DepartmentId)  " +
                   "VALUES (?,?,?,?,?)" ,PreparedStatement.RETURN_GENERATED_KEYS                     
            );
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());
            
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
    public void update(Seller obj) {
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(
                          "UPDATE seller "+
                          "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "+
                          "WHERE Id = ? "        
            );
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getEmail());
            ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            ps.setDouble(4, obj.getBaseSalary());
            ps.setInt(5, obj.getDepartment().getId());
            ps.setInt(6, obj.getId());
            
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
           ps = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
           ps.setInt(1, id);
           ps.execute();
           
       } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
           DB.closePreparedStatement(ps);
       }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            ps = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                   +"FROM seller INNER JOIN department "
                   +"ON seller.DepartmentId = department.Id "
                   +"WHERE seller.Id = ? "
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Department dep = instantiateDepartment(rs); 
                Seller seller = instatiateSaller(rs,dep);
                return seller;
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
    public List<Seller> findAll() {
       PreparedStatement ps = null;
         ResultSet rs = null;
         
         try{
             ps = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                    +"FROM seller INNER JOIN department "
                    +"ON seller.DepartmentId = department.Id "
                    +"ORDER BY Name "
             );
 
             rs = ps.executeQuery();   
                  List<Seller> list = new ArrayList<>();
                  while(rs.next()){
                      Department dep = instantiateDepartment(rs);
                      list.add(instatiateSaller(rs, dep));
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
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    private Seller instatiateSaller(ResultSet rs, Department dep) throws SQLException {
        Seller seller = new Seller(
                        rs.getInt("Id"),
                        rs.getString("Name"), 
                        rs.getString("Email"), 
                        rs.getDate("BirthDate"),
                        rs.getDouble("BaseSalary") , 
                        dep);
                return seller;
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
         PreparedStatement ps = null;
         ResultSet rs = null;
         
         try{
             ps = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                    +"FROM seller INNER JOIN department "
                    +"ON seller.DepartmentId = department.Id "
                    +"WHERE DepartmentId = ? "
                    +"ORDER BY Name "
             );
             ps.setInt(1, department.getId());
             rs = ps.executeQuery();
             
                  
                  List<Seller> list = new ArrayList<>();
                  Map<Integer, Department> map = new HashMap<>();
                  while(rs.next()){
                      Department dep = map.get(rs.getInt("DepartmentId"));
                      if (dep == null) {
                          dep = instantiateDepartment(rs);
                          map.put(rs.getInt("DepartmentId"), dep);
                      }
                      list.add(instatiateSaller(rs, dep));
                 }
                return list;  
     
         } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }finally{
             DB.closePreparedStatement(ps);
             DB.closeResultSet(rs);
         }
    }
    
}
