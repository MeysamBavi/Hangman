import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class HangmanGame {

    public static void main(String[] args) {
        new HangmanGame();
    }

    private final Scanner sc;
    private boolean color_mode;
    private Player player;
    private static ArrayList<Player> dataBase;
    private static StringBuilder leaderboard;
    private static final String dataBaseFileName = "C:\\Users\\meysa\\IdeaProjects\\HangmanGame\\src\\dataBase.txt";
    private static final String leaderboardFileName = "C:\\Users\\meysa\\IdeaProjects\\HangmanGame\\src\\leaderboard.txt";
    public static final int DEFAULT = 0;
    public static final int RED = 1;
    public static final int RED_BOLD = 2;
    public static final int GREEN = 3;
    public static final int GREEN_BOLD = 4;
    public static final int YELLOW = 5;
    public static final int YELLOW_BOLD = 6;
    public static final int BLUE = 7;
    public static final int BLUE_BOLD = 8;
    public static final int MAGENTA = 9;
    public static final int MAGENTA_BOLD = 10;
    public static final int CYAN = 11;
    public static final int CYAN_BOLD = 12;
    private static final String[] colors = {
            "[0m",
            "[0;31m",
            "[1;31m",
            "[0;32m",
            "[1;32m",
            "[0;33m",
            "[1;33m",
            "[0;34m",
            "[1;34m",
            "[0;35m",
            "[1;35m",
            "[0;36m",
            "[1;36m",
    };


    public static boolean loadData() {
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new File(dataBaseFileName));
        } catch (IOException e) {
            dataBase = new ArrayList<>(5);
            return false;
        }
        int n = fileScanner.nextInt();
        fileScanner.nextLine();
        dataBase = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Player p = new Player();
            p.setUsername(fileScanner.nextLine());
            p.setPassword(fileScanner.nextLine());
            p.setScore(Integer.parseInt(fileScanner.nextLine()));
            dataBase.add(p);
        }
        return true;
    }

    public static void saveData() {

        PrintStream fileStream, lbStream;
        try {
            fileStream = new PrintStream(dataBaseFileName);
            lbStream = new PrintStream(leaderboardFileName);
        } catch (IOException e) {
            return;
        }
        lbStream.print(updateLeaderboard());
        fileStream.println(dataBase.size());
        for (Player p : dataBase) {
            fileStream.println(p.getUsername());
            fileStream.println(p.getPassword());
            fileStream.println(p.getScore());
        }
    }

    public static String updateLeaderboard() {
        sort();
        leaderboard = new StringBuilder();
        for (Player p : dataBase) {
            leaderboard.append(p.getUsername());
            leaderboard.append("-".repeat(Math.max(0, 25 - p.getUsername().length())));
            leaderboard.append(p.getScore()).append("\n");
        }
        return leaderboard.toString();
    }

    public static String getLeaderboard() {
        return leaderboard.toString();
    }

    private static void sort() {
        for (int i = 0; i < dataBase.size() - 1; i++) {
            int last = dataBase.size()-1-i;
            int indexOfMin = 0;
            for (int j = 1; j < dataBase.size() - i; j++) {
                if (dataBase.get(j).getScore() < dataBase.get(indexOfMin).getScore()) {
                    indexOfMin = j;
                }
            }
            Player temp = dataBase.get(last);
            dataBase.set(last, dataBase.get(indexOfMin));
            dataBase.set(indexOfMin, temp);
        }
    }

     static void change_color(int color) {
        if (color >= 0 && color <= 12) {
            System.out.print("\033");
            System.out.printf("%s", colors[color]);
        }
    }

    public void println(int color, String str) {
        if (color_mode) {
            change_color(color);
            System.out.println(str);
            change_color(DEFAULT);
        } else  {
            System.out.println(str);
        }
    }

    public void print(int color, String str) {
        if (color_mode) {
            change_color(color);
            System.out.print(str);
            change_color(DEFAULT);
        } else  {
            System.out.print(str);
        }
    }

    public void printErrorBar(TargetWord tw) {
        print(DEFAULT, "|");
        int i;
        for (i = 0; i < tw.getErrors(); i++) {
            print(RED_BOLD, "X");
            print(DEFAULT, "|");
        }
        for (; i < tw.getMaxErrors(); i++) {
            print(GREEN_BOLD, "V");
            print(DEFAULT, "|");
        }
        println(DEFAULT, "");
    }

    void home() {
        player = null;
        print(RED_BOLD, "1. ");
        println(DEFAULT, "Sign Up");
        print(RED_BOLD, "2. ");
        println(DEFAULT, "Login");
        print(RED_BOLD, "3. ");
        println(DEFAULT, "Save and Exit");
        print(GREEN, "Choose an option: ");
        String inp = sc.nextLine();
        while (!inp.equals("1") && !inp.equals("2") && !inp.equals("3")) {
            print(RED_BOLD, "Invalid input. Try again: ");
            inp = sc.nextLine();
        }
        switch (inp) {
            case "1":
                signup();
                break;
            case "2":
                login();
                break;
            case "3":
                println(YELLOW, "Saving...");
                saveData();
                println(BLUE, "Done.");
                System.exit(0);
                break;
        }
    }

    void signup() {
        Player newPlayer = new Player();
        boolean unique;
        String un;
        do {
            unique = true;
            print(GREEN, "Enter a username: ");
            un = sc.nextLine();
            for (Player p : dataBase) {
                if (p.getUsername().equals(un)) {
                    unique = false;
                    break;
                }
            }
            if (un.length()==0) {
                unique = false;
            }
            if (!unique) {
                println(RED,  un + " is already taken. Enter another username.");
            }
        } while (!unique);
        newPlayer.setUsername(un);

        print(GREEN, "Enter a password: ");
        String pass = sc.nextLine();
        while (!newPlayer.setPassword(pass)) {
            println(RED, "Invalid password.");
            print(GREEN, "Enter a password: ");
            pass = sc.nextLine();
        }
        dataBase.add(newPlayer);
        this.player = newPlayer;
        println(CYAN, "Signed up successfully!");
        home2();
    }

    void login() {
        String un, pass;
        boolean found = false;
        print(GREEN, "Enter your username: ");
        un = sc.nextLine();
        print(GREEN, "Enter your password: ");
        pass = sc.nextLine();
        for (Player p : dataBase) {
            if (p.getUsername().equals(un) && p.getPassword().equals(pass)) {
                found = true;
                this.player = p;
                break;
            }
        }
        if (!found) {
            println(RED, "Username/password is wrong. What to do? (1. sign up / else. Re-enter credentials) ");
            String inp = sc.nextLine();
            if (inp.equals("1")) {
                signup();
            } else {
                login();
            }
        } else {
            home2();
        }
    }

    void home2() {
        println(YELLOW, "Welcome " + player.getUsername() + "!");
        print(MAGENTA_BOLD, "1. ");
        println(DEFAULT, "Start Game");
        print(MAGENTA_BOLD, "2. ");
        println(DEFAULT, "Show Leaderboard");
        print(MAGENTA_BOLD, "3. ");
        println(DEFAULT, "Log out");
        print(GREEN, "Choose an option: ");
        String inp = sc.nextLine();
        while (!inp.equals("1") && !inp.equals("2") && !inp.equals("3")) {
            print(RED_BOLD, "Invalid input. Try again: ");
            inp = sc.nextLine();
        }
        switch (inp) {
            case "1":
                play();
                break;
            case "2":
                println(DEFAULT, updateLeaderboard());
                home2();
                break;
            case "3":
                home();
                break;
        }
    }

    HangmanGame() {
        if (loadData()) {
            System.out.println("File loaded successfully.");
        } else {
            System.out.println("File not found.");
        }
        sc = new Scanner(System.in);
        System.out.print("Enable color mode? (y/n) ");
        String inp = sc.nextLine();
        if (inp.equals("y")) {
            color_mode = true;
            println(BLUE, "Color mode enabled!");
        }
        home();
    }

    public void play() {
        Hangman hangman = new Hangman();
        TargetWord tw = new TargetWord();
        boolean gotHint = false;
        while (!tw.isOver()) {
            hangman.setStage(tw.getHangmanStage());
            hangman.draw();
            if (color_mode) {
                printErrorBar(tw);
            }
            println(BLUE,"Errors: " + tw.getErrors() + " out of " + tw.getMaxErrors());
            println(YELLOW,"Guessed characters: " + tw.getGuessedChars());
            println(DEFAULT, "");
            println(CYAN,tw.toString());

            String input;
            boolean valid_input = false;
            do {
                print(MAGENTA,"Enter your guess" + (gotHint || player.getScore() < 10 ? ": " : "\nor type \'hint\' for a random hint (costs 10 points): "));
                input = sc.nextLine();
                if (input.equals("hint")) {
                    if (gotHint) {
                      println(RED, "You can only get 1 hint.");
                    } else if (player.hint()) {
                        println(BLUE_BOLD, "hint: " + tw.getHint());
                        gotHint = true;
                    } else {
                        println(RED, "Not enough points.");
                    }
                } else if (input.length() != 1 || !(input.charAt(0) <= 'z' && input.charAt(0) >= 'a')) {
                    println(RED,"Invalid Input.");
                } else {
                    valid_input = true;
                }
            } while (!valid_input);

            switch (tw.guess(input.charAt(0))) {
                case TargetWord.WRONG_GUESS:
                    println(RED,"Your Guess was wrong.");
                    break;
                case TargetWord.ALREADY_GUESSED:
                    println(YELLOW,"You have entered this character before.");
                    break;
                case TargetWord.CORRECT_GUESS:
                    println(GREEN_BOLD,"Your guess was correct!");
                    break;
                case TargetWord.GAME_IS_OVER:
                    println(MAGENTA_BOLD,"GAME IS OVER!!");
                    if (tw.isGuessed()) {
                        println(GREEN_BOLD, "YOU WON!\nEarned 5 points.");
                        player.setScore(player.getScore()+5);
                    } else if (tw.isFailed()){
                        println(RED_BOLD,"YOU FAILED!");
                        hangman.setStage(tw.getHangmanStage());
                        hangman.draw();
                    }
                    println(CYAN,"Answer: " + tw);
                    break;
                default:
                    System.out.println("something is wrong.");
                    break;
            }
        }
        println(DEFAULT,updateLeaderboard());
        home();
    }

}

class Hangman {
    private int stage = 0;
    void draw() {
        System.out.println("----");
        System.out.println("|" + (stage >= 1 ? "  |" : ""));
        System.out.println("|" + (stage >= 2 ? "  O" : ""));
        System.out.println("|" + (stage >= 3 ? " /" : "") + (stage >= 4 ? "|" : "") + (stage >= 5 ? "\\" : ""));
        System.out.println("|" + (stage >= 6 ? " /" : "") + (stage >= 7 ? "\\" : ""));
        System.out.println("|");
    }
    public void setStage(int stage) {
        if (stage >= 0 && stage <= 7) {
            this.stage = stage;
        }
    }
    public void reset() {
        stage = 0;
    }
    public int getStage() {
        return stage;
    }
}

class TargetWord {
    private static String[] words = {"tehran", "pizza", "banana", "new york", "advanced programming", "michael jordan",
            "lionel messi", "apple", "macaroni", "university", "intel", "kitten", "python", "java",
            "data structures", "algorithm", "assembly", "basketball", "hockey", "leader", "javascript",
            "toronto", "united states of america", "psychology", "chemistry", "breaking bad", "physics",
            "abstract classes", "linux kernel", "january", "march", "time travel", "twitter", "instagram",
            "dog breeds", "strawberry", "snow", "game of thrones", "batman", "ronaldo", "soccer",
            "hamburger", "italy", "greece", "albert einstein", "hangman", "clubhouse", "call of duty",
            "science", "theory of languages and automata"};
    public static final int WRONG_GUESS = 0;
    public static final int ALREADY_GUESSED = 1;
    public static final int CORRECT_GUESS = 2;
    public static final int GAME_IS_OVER = 3;
    private String word;
    private StringBuilder displayed;
    private boolean guessed;
    private boolean failed;
    private int maxErrors;
    private int errors;
    private ArrayList<Character> guessedChars;
    private int numberOfLetters;
    TargetWord() {
        word = words[(int)(Math.random()*words.length)];
        guessedChars = new ArrayList<Character>();
        numberOfLetters = numberOfLetters(word);
        maxErrors = (numberOfLetters > 9 ? 14 : 7);
        displayed = new StringBuilder(word);
        for (int i = 0; i < displayed.length(); i++) {
            if (Character.isLetter(displayed.charAt(i))) {
                displayed.setCharAt(i,'-');
            }
        }
    }
    @Override
    public String toString() {
        if (isOver()) {
            return word;
        }
        return displayed.toString();
    }

    private int numberOfLetters(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                n++;
            }
        }
        return n;
    }

    public int guess(char c) {
        if (isOver()) {
            return GAME_IS_OVER;
        }
        if (guessedChars.contains(c)) {
            return ALREADY_GUESSED;
        }
        guessedChars.add(c);
        boolean right = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i)==c) {
                right = true;
                displayed.setCharAt(i , c);
            }
        }
        if (right) {
            if (displayed.toString().equals(word)) {
                guessed = true;
                return GAME_IS_OVER;
            }
            return CORRECT_GUESS;
        }
        errors++;
        if (errors==maxErrors) {
            failed = true;
            return GAME_IS_OVER;
        }
        return WRONG_GUESS;
    }

    public char getHint() {
        int n = 0, rand;
        for (int i = 0; i < displayed.length(); i++) {
            if (displayed.charAt(i)=='-') {
                n++;
            }
        }
        rand = (int)(Math.random()*n);
        for (int i = 0; i < displayed.length(); i++) {
            if (displayed.charAt(i)=='-') {
                if (rand == 0) {
                    return word.charAt(i);
                } else {
                    rand--;
                }
            }
        }
        return '!';
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public int getErrors() {
        return errors;
    }

    public String getGuessedChars() {
        return guessedChars.toString();
    }

    public boolean isGuessed() {
        return guessed;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isOver() {
        return failed||guessed;
    }

    public int getHangmanStage() {
        return errors/(maxErrors/7);
    }

}

class Player {

    private String username = null;
    private String password = null;
    private int score = 0;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

     boolean setPassword(String password) {
        Pattern passwordPattern = Pattern.compile("(?=.{6,})([a-zA-Z0-9]*[!@#$%^&*_-][a-zA-Z0-9]*)");
        if (passwordPattern.matcher(password).matches()) {
            this.password = password;
            return true;
        }
        return false;
    }

    String getPassword() {
        return password;
    }

    void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public boolean hint() {
        if (score>=10) {
            score-=10;
            return true;
        }
        return false;
    }

}