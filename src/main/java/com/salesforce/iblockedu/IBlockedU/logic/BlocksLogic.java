package com.salesforce.iblockedu.IBlockedU.logic;

import com.salesforce.iblockedu.IBlockedU.dal.BlocksDal;
import com.salesforce.iblockedu.IBlockedU.dal.CarsDal;
import com.salesforce.iblockedu.IBlockedU.dal.UsersDal;
import com.salesforce.iblockedu.IBlockedU.model.Block;
import com.salesforce.iblockedu.IBlockedU.model.Car;
import com.salesforce.iblockedu.IBlockedU.model.User;

import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;

/**
 * Created by doron.levi on 29/11/2017.
 */
public class BlocksLogic {
    private UsersDal usersDal;
    private BlocksDal blocksDal;
    private CarsDal carsDal;

    public BlocksLogic(UsersDal usersDal, BlocksDal blocksDal,CarsDal carsDal) {
        this.usersDal = usersDal;
        this.blocksDal = blocksDal;
        this.carsDal = carsDal;
    }

    public String unBlock(String email) {
        String error = "";
        User user = usersDal.getUserByEmail(email);
        if (user.isActive()) {
            long timeInMillis = Calendar.getInstance().getTime().getTime();
            blocksDal.updateExitHour(user,new Date(timeInMillis));
            blocksDal.removeBlock(user);
        } else {
            error = String.format("Error: No active user found for: %s", email);
        }
        return error;
    }

    public String block(String email, String licensePlate, Date exitTime) {

        String error = "";

        User user = usersDal.getUserByEmail(email);

        if (user.isActive()) {

            Car car = carsDal.getCarByLicensePlate(licensePlate);

            if (car.getId() != -1) {

                Block block = new Block();
                block.setBlockerId(user.getId());
                block.setBlockingDate(new Date(Instant.now().toEpochMilli()));
                block.setBlockerExitTime(exitTime);
                block.setBlockedCarId(car.getId());
                block.setActive(true);
                block.setBlockedId(car.getOwnerId());

                blocksDal.addBlock(block);
            } else
                error = "Error: No Car found for license plate: " + licensePlate;
        } else {
            error = "Error: No user found for " + email;
        }

        return error;
    }

    public String getMyBlocker(String email) {
        String blocker_email = "";
        User user = usersDal.getUserByEmail(email);
        if (user.isActive()) {
            Block block = blocksDal.getMyBlocker(user);
            blocker_email = usersDal.getUserById(block.getBlockerId()).getEmail();
        } else {
            blocker_email = String.format("Error: No active user found for: %s", email);
        }

        return blocker_email;
    }
}
