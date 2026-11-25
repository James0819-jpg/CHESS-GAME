package f0;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class AccountManager {
    private static final String FILE_NAME = "data.txt";
    private HashMap<String, String> accounts = new HashMap<>();

    public AccountManager() {
        loadAccounts();
    }

    private void loadAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) accounts.put(parts[0], parts[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String user, String pass) {
        return accounts.containsKey(user) && accounts.get(user).equals(pass);
    }

    public boolean register(String user, String pass) {
        if (accounts.containsKey(user)) return false;
        accounts.put(user, pass);
        saveAccount(user, pass);
        return true;
    }

    private void saveAccount(String user, String pass) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(user + ":" + pass);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}