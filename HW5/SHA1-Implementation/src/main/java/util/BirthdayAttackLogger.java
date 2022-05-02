package util;

import attacks.BirthdayAttack;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public abstract class BirthdayAttackLogger {
    public static void saveBirthdayAttackData(BirthdayAttack birthdayAttack) {
        File file = new File("src/main/resources/birthday-attack-data/data.json");

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, birthdayAttack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BirthdayAttack loadBirthdayAttackData() {
        File file = new File("src/main/resources/birthday-attack-data/data.json");
        BirthdayAttack birthdayAttack = new BirthdayAttack();

        ObjectMapper mapper = new ObjectMapper();
        try {
            birthdayAttack = mapper.readValue(file, BirthdayAttack.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done loading the attack data.");
        return birthdayAttack;
    }
}
