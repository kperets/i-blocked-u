package com.salesforce.iblockedu.IBlockedU.dal;

import com.salesforce.iblockedu.IBlockedU.exceptions.IBlockedUException;
import com.salesforce.iblockedu.IBlockedU.model.Car;
import com.salesforce.iblockedu.IBlockedU.model.CarOwnerInfo;
import com.salesforce.iblockedu.IBlockedU.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarsDal extends BaseDal<Car> {
    public CarsDal(DataSource dataSource) {
        super(dataSource);
    }

    public void createCar(Car car) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(String.format("INSERT INTO CARS (COLOR, MODEL, OWNER_ID, LICENSE_PLATE) VALUES (\'%s\', \'%s\', \'%d\', \'%s\')",
                    car.getColor(), car.getModel(), car.getOwnerId(), car.getLicensePlate()));

        } catch (Exception e) {
            ///log
            throw new IBlockedUException(e);
        }
    }

    public Car getCarByLicensePlate(String licensePlate) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM CARS WHERE LICENSE_PLATE = '%s'",licensePlate));

            if(rs.next()) {
                return getCarFromRecord(rs);
            } else
                return Car.getEmpty();

        } catch (Exception e) {
            ///log
            throw new IBlockedUException(e);
        }

    }

    public List<Car> getCarByUserId(int id) {
        List<Car> cars = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM CARS WHERE OWNER_ID = '%d'", id));

            while (rs.next()) {
                Car car = getCarFromRecord(rs);
                cars.add(car);
            }

            return cars;

        } catch (Exception e) {
            ///log
            throw new IBlockedUException(e);
        }
    }

    public List<CarOwnerInfo> getAllCarsOwnersInfo() {

        List<CarOwnerInfo> carInfos = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name,license_plate from users,cars where cars.owner_id = users.id and users.active = true");

            CarOwnerInfo carInfo;

            while (rs.next()) {
                carInfo = getCarOwnerInfoFromRecord(rs);
                carInfos.add(carInfo);
            }

        } catch (Exception e) {
            ///log
            throw new IBlockedUException(e);
        }

        return carInfos;

    }


    public User getUserByCarId(Car car) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT OWNER_ID FROM CARS WHERE ID = %d", car.getId()));
            int ownerId = -1;

            if (rs.next()) {
                ownerId = rs.getInt("OWNER_ID");
            }

            ResultSet resultSet = stmt.executeQuery(String.format("SELECT * FROM USERS WHERE ID = %d", ownerId));

            if (resultSet.next())
                return UsersDal.getUserFromRecord(resultSet);

            return User.getEmpty();

        } catch (Exception e) {
            ///log
            throw new IBlockedUException();
        }

    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CARS");
            Car car;
            while (rs.next()) {
                car = getCarFromRecord(rs);
                cars.add(car);
            }

        } catch (Exception e) {
            ///log
            throw new IBlockedUException(e);
        }

        return cars;
    }

    private static Car getCarFromRecord(ResultSet rs)  throws SQLException {
        Car car;
        car = new Car();
        car.setId(rs.getInt("ID"));
        car.setColor(rs.getString("COLOR"));
        car.setModel(rs.getString("MODEL"));
        car.setOwnerId(rs.getInt("OWNER_ID"));
        car.setLicensePlate(rs.getString("LICENSE_PLATE"));

        return car;
    }

    private static CarOwnerInfo getCarOwnerInfoFromRecord(ResultSet rs)  throws SQLException {

        return new CarOwnerInfo(rs.getString("name"),rs.getString("LICENSE_PLATE"));
    }


}
