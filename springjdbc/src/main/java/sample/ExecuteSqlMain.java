package sample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import sample.config.DataSourceConfig;
import sample.config.TemplateConfig;
import sample.springjdbc.business.domain.Owner;
import sample.springjdbc.business.domain.Pet;

public class ExecuteSqlMain {

    public static void main(String[] args) {
    	
    	// Spring 컨터이너 생성        
    	//XML로 Bean을 정의한 경우
    	ApplicationContext ctx = new ClassPathXmlApplicationContext("sample/config/spring-db.xml");

    	//JavaConfig로 Bean을 정의한 경우
    	//ApplicationContext ctx = new AnnotationConfigApplicationContext(
        //        TemplateConfig.class, DataSourceConfig.class);
        
        // JdbcTemplate 와 NamedParameterJdbcTemplate 오브젝트를 취득
        JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = ctx.getBean(NamedParameterJdbcTemplate.class);

        // SELECT 문 ~ 도메인으로 변환안하는 경우
        // queryForInt 메서드
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PET", Integer.class);
        System.out.println(count);

        
        String ownerName = "홍길동";
        count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM PET WHERE OWNER_NAME=?", Integer.class, ownerName);
        System.out.println(count);
        
        // queryForObject 메서드
        int id = 1;
        String petName = jdbcTemplate.queryForObject(
                "SELECT PET_NAME FROM PET WHERE PET_ID=?", String.class, id);
        System.out.println(petName);
        
        Date birthDate = jdbcTemplate.queryForObject(
                "SELECT BIRTH_DATE FROM PET WHERE PET_ID=?", Date.class, id);
        System.out.println(birthDate);
        
        
        // queryForMap 메서드
        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM PET WHERE PET_ID=?", id);
        System.out.println(map.get("PET_NAME"));
        System.out.println(map.get("OWNER_NAME"));
        
        // queryForList 메서드
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(
                " SELECT * FROM PET WHERE OWNER_NAME=?", ownerName);
        System.out.println(mapList.get(0).get("PET_NAME"));
        System.out.println(mapList.get(0).get("OWNER_NAME"));
        
        
        List<Integer> priceList = jdbcTemplate.queryForList(
                "SELECT PRICE FROM PET WHERE OWNER_NAME=?", Integer.class, ownerName);
        
        System.out.println(priceList.get(0));
        
        
        // SELECT 문 ~ 도메인으로 변환하는 경우
        // queryForObject 메서드
        // RowMapper을 익명클라스로하는 경우
        Pet pet = jdbcTemplate.queryForObject(
                "SELECT * FROM PET WHERE PET_ID=?"
                , new RowMapper<Pet>() {
                    public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Pet p = new Pet();
                        p.setPetId((Integer)rs.getObject("PET_ID"));
                        p.setPetName(rs.getString("PET_NAME"));
                        p.setOwnerName(rs.getString("OWNER_NAME"));
                        p.setPrice((Integer)rs.getObject("PRICE"));
                        p.setBirthDate(rs.getDate("BIRTH_DATE"));
                        return p;
                        }}
                , id); 
        System.out.println(pet.getPetName());
        System.out.println(pet.getOwnerName());
        
        // RowMapper을 익명클래스로 하지 않는 경우
        class MyRowMapper implements RowMapper<Pet> {
            public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
                Pet p = new Pet();
                p.setPetId(rs.getInt("PET_ID"));
                p.setPetName(rs.getString("PET_NAME"));
                p.setOwnerName(rs.getString("OWNER_NAME"));
                p.setPrice(rs.getInt("PRICE"));
                p.setBirthDate(rs.getDate("BIRTH_DATE"));
                return p;
            }
        }
        pet = jdbcTemplate.queryForObject(
                " SELECT * FROM PET WHERE PET_ID=?"
                ,new MyRowMapper()
                ,id);
        System.out.println(pet.getPetName());
        System.out.println(pet.getOwnerName());
        
        // query 메서드
        List<Pet> petList = jdbcTemplate.query(
                " SELECT * FROM PET WHERE OWNER_NAME=?"
                , new RowMapper<Pet>() {
                public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Pet p = new Pet();
                    p.setPetId(rs.getInt("PET_ID"));
                    p.setPetName(rs.getString("PET_NAME"));
                    p.setOwnerName(rs.getString("OWNER_NAME"));
                    p.setPrice(rs.getInt("PRICE"));
                p.setBirthDate(rs.getDate("BIRTH_DATE"));
                    return p;
                }}
            , ownerName);
        System.out.println(petList.get(0).getPetName());
        System.out.println(petList.get(0).getOwnerName());

        // BeanPropertyRowMapper을 사용해서 도메인으로 변환을 자동화
        pet = jdbcTemplate.queryForObject(
                " SELECT * FROM PET WHERE PET_ID=?"
                , new BeanPropertyRowMapper<Pet>(Pet.class)
                , id);
        System.out.println(pet.getPetName());
        System.out.println(pet.getOwnerName());

        // ResultSetExtractor을 사용한 도메인 변환
        // 부모 도메인가 하나인 경우
        Owner owner = jdbcTemplate.query(
                " SELECT * FROM OWNER O INNER JOIN PET P ON O.OWNER_NAME=P.OWNER_NAME WHERE O.OWNER_NAME=?"
                , new ResultSetExtractor<Owner>() {
                    public Owner extractData(ResultSet rs) throws SQLException, DataAccessException {
                        if (!rs.next()) {
                            return null;
                        }
                        Owner owner = new Owner();
                        owner.setOwnerName(rs.getString("OWNER_NAME"));
                        do {
                            Pet pet = new Pet();
                            pet.setPetId(rs.getInt("PET_ID"));
                            pet.setPetName(rs.getString("PET_NAME"));
                            pet.setOwnerName(rs.getString("OWNER_NAME"));
                            pet.setPrice(rs.getInt("PRICE"));
                            pet.setBirthDate(rs.getDate("BIRTH_DATE"));
                            owner.getPetList().add(pet);
                        } while(rs.next());
                        return owner;
                    }}
                , ownerName);
        System.out.println(owner.getOwnerName());
        System.out.println(owner.getPetList().get(0).getPetName());
        System.out.println(owner.getPetList().get(0).getOwnerName());
        
        // 부모 도메인가 복수인 경우
        List<Owner> ownerList = jdbcTemplate.query(
                " SELECT * FROM OWNER O INNER JOIN PET P ON O.OWNER_NAME=P.OWNER_NAME ORDER BY OWNER_NAME"
                , new ResultSetExtractor<List<Owner>>() {
                    public List<Owner> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Owner> result = new ArrayList<Owner>();
                        Owner owner = null;
                        String currentPk = "";
                        while (rs.next()) {
                            String ownerName = rs.getString("OWNER_NAME");
                            if (!ownerName.equals(currentPk)) {
                                owner = new Owner();
                                owner.setOwnerName(rs.getString("OWNER_NAME"));
                                currentPk = ownerName;
                                result.add(owner);
                            }
                            Pet pet = new Pet();
                            pet.setPetId(rs.getInt("PET_ID"));
                            pet.setPetName(rs.getString("PET_NAME"));
                            pet.setOwnerName(rs.getString("OWNER_NAME"));
                            pet.setPrice(rs.getInt("PRICE"));
                            pet.setBirthDate(rs.getDate("BIRTH_DATE"));
                            owner.getPetList().add(pet);
                        }
                        return result;
                    }}
                );
        System.out.println(ownerList.get(0).getOwnerName());
        System.out.println(ownerList.get(0).getPetList().get(0).getPetName());
        System.out.println(ownerList.get(0).getPetList().get(0).getOwnerName());
        
        
        // INSERT/UPDATE/DELETE 문
        pet = new Pet();
        pet.setPetId(99);
        pet.setPetName("나비");
        pet.setOwnerName("홍길동");
        pet.setPrice(10000);
        pet.setBirthDate(new Date());        
        jdbcTemplate.update(
                "INSERT INTO PET (PET_ID, PET_NAME, OWNER_NAME, PRICE, BIRTH_DATE) VALUES (?, ?, ?, ?, ?)"
                , pet.getPetId(), pet.getPetName(), pet.getOwnerName(), pet.getPrice(), pet.getBirthDate());
        
        jdbcTemplate.update(
                "UPDATE PET SET PET_NAME=?, OWNER_NAME=?, PRICE=?, BIRTH_DATE=? WHERE PET_ID=?"
                , pet.getPetName(), pet.getOwnerName(), pet.getPrice(), pet.getBirthDate(), pet.getPetId());
        
        jdbcTemplate.update("DELETE FROM PET WHERE PET_ID=?", pet.getPetId());

        // NamedParameterJdbcTemplate를 사용
        // 메서드 체인에 기술하는 경우
        namedParameterJdbcTemplate.update(
                " INSERT INTO PET (PET_ID, PET_NAME, OWNER_NAME, PRICE, BIRTH_DATE)" +
                    " VALUES (:PET_ID, :PET_NAME, :OWNER_NAME, :PRICE, :BIRTH_DATE)"
                , new MapSqlParameterSource()
                .addValue("PET_ID", pet.getPetId())
                .addValue("PET_NAME", pet.getPetName())
                .addValue("OWNER_NAME", pet.getOwnerName())
                .addValue("PRICE", pet.getPrice())
                .addValue("BIRTH_DATE", pet.getBirthDate())
            );
        
        jdbcTemplate.update("DELETE FROM PET WHERE PET_ID=?", pet.getPetId());
        
        // 메서드 체인에 기술하지 않는 경우
        MapSqlParameterSource map2 = new MapSqlParameterSource();
        map2.addValue("PET_ID", pet.getPetId());
        map2.addValue("PET_NAME", pet.getPetName());
        map2.addValue("OWNER_NAME", pet.getOwnerName());
        map2.addValue("PRICE", pet.getPrice());
        map2.addValue("BIRTH_DATE", pet.getBirthDate());
        namedParameterJdbcTemplate.update(
            " INSERT INTO PET (PET_ID, PET_NAME, OWNER_NAME, PRICE, BIRTH_DATE)" +
                " VALUES (:PET_ID, :PET_NAME, :OWNER_NAME, :PRICE, :BIRTH_DATE)"
            ,map2
        );
        
        jdbcTemplate.update("DELETE FROM PET WHERE PET_ID=?", pet.getPetId());

        // BeanPropertySqlParameterSource를 사용해서 도메인에서 파라메터변화을 자동화
        BeanPropertySqlParameterSource beanProps = new BeanPropertySqlParameterSource(pet);
        namedParameterJdbcTemplate.update(
            " INSERT INTO PET (PET_ID, PET_NAME, OWNER_NAME, PRICE, BIRTH_DATE)" +
                " VALUES (:petId, :petName, :ownerName, :price, :birthDate)"
            ,beanProps
        );
        
        jdbcTemplate.update("DELETE FROM PET WHERE PET_ID=?", pet.getPetId());
        
        // 배치 업데이트, 프로시져 콜
        // batchUpdate 메서드를 사용한 배치 업데이트
        final ArrayList<Pet> pList = new ArrayList<Pet>();
        pet = new Pet();
        pet.setPetId(1);
        pet.setOwnerName("owner001");
        pList.add(pet);
        pet = new Pet();
        pet.setPetId(2);
        pet.setOwnerName("owner002");
        pList.add(pet);
        pet = new Pet();
        pet.setPetId(3);
        pet.setOwnerName("owner003");
        
        pList.add(pet);
                
        //IN구
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("ids", ids);
        petList = namedParameterJdbcTemplate.query(
            "SELECT * FROM PET WHERE PET_ID IN (:ids)", 
            param, 
            new RowMapper<Pet>() {
                public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Pet p = new Pet();
                    p.setPetId(rs.getInt("PET_ID"));
                    p.setPetName(rs.getString("PET_NAME"));
                    p.setOwnerName(rs.getString("OWNER_NAME"));
                    p.setPrice((Integer)rs.getObject("PRICE"));
                    p.setBirthDate(rs.getDate("BIRTH_DATE"));
                    return p;
                }
            }
        );
        
        System.out.println("IN구");
        for (Pet p : petList) {
            System.out.println(p.getPetId());
        }
        
        
        //JdbcTemplate의 batchUpdate
        int[] num = jdbcTemplate.batchUpdate("UPDATE PET SET OWNER_NAME=? WHERE PET_ID=?", new BatchPreparedStatementSetter() {            
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, pList.get(i).getOwnerName());
                ps.setInt(2, pList.get(i).getPetId());
            }
            @Override
            public int getBatchSize() {
                return pList.size();
            }
        });
        
        //NamedParameterJdbcTemplateのbatchUpdate
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(pList.toArray());
        num = namedParameterJdbcTemplate.batchUpdate(
                "UPDATE PET SET OWNER_NAME=:ownerName WHERE PET_ID=:petId", batch);        
        
        
        // SimpleJdbcCall을 사용한 프로시져 콜
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate.getDataSource())
        .withProcedureName("CALC_PET_PRICE")
        .withoutProcedureColumnMetaDataAccess()
        .declareParameters(
            new SqlParameter("IN_PET_ID", Types.INTEGER),
            new SqlOutParameter("OUT_PRICE", Types.INTEGER)
        );        
        MapSqlParameterSource in = new MapSqlParameterSource()
                        .addValue("IN_PET_ID", id);
        Map<String, Object> out = call.execute(in);
        int price = (Integer)out.get("OUT_PRICE");
        System.out.println(price);
    }
    
}
