import java.util.*;

public class ATMSimulator {

    // account data kept in arrays (sorted by account number so binary search works)
    static int[] accNo = {1001, 1002, 1003, 1004, 1005};
    static String[] holder = {"Praveen", "Rahul", "Anjali", "Sneha", "Vikram"};
    static int[] pin = {1111, 2222, 3333, 4444, 5555};
    static int[] balance = {15000, 8000, 25000, 500, 12000};

    // each account has its own stack of transactions (latest on top)
    static Stack<String>[] history = new Stack[5];

    static int[] notes = {500, 200, 100};   // note denominations for greedy
    static final int LIMIT = 20000;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        for (int i = 0; i < history.length; i++) {
            history[i] = new Stack<>();
        }

        System.out.println("===== ATM SIMULATOR =====");
        System.out.println("Sample accounts: 1001-1005 (pin = 1111, 2222 ... in the same order)");

        // customers waiting in line at the ATM
        Queue<Integer> line = new LinkedList<>();
        System.out.println("\nAdd customers to the queue (enter account number, 0 to stop):");
        while (true) {
            System.out.print("Account no: ");
            int no = sc.nextInt();
            if (no == 0) break;
            line.add(no);
        }

        if (line.isEmpty()) {
            System.out.println("Nobody in the queue. Bye!");
            return;
        }

        System.out.println("\n" + line.size() + " customer(s) waiting.");

        while (!line.isEmpty()) {
            int current = line.poll();
            System.out.println("\n-------------------------------");
            System.out.println("Now serving account: " + current + "  (waiting after this: " + line.size() + ")");
            serve(current);
        }

        System.out.println("\nQueue is empty. ATM closed.");
    }

    static void serve(int no) {
        int idx = findAccount(no);
        if (idx == -1) {
            System.out.println("Account not found.");
            return;
        }

        // 3 tries for the pin
        boolean ok = false;
        for (int tries = 1; tries <= 3; tries++) {
            System.out.print("Enter PIN: ");
            int p = sc.nextInt();
            if (p == pin[idx]) {
                ok = true;
                break;
            }
            System.out.println("Wrong PIN. Attempts left: " + (3 - tries));
        }
        if (!ok) {
            System.out.println("Card blocked for this session.");
            return;
        }

        System.out.println("Welcome, " + holder[idx] + "!");
        int choice;
        do {
            System.out.println("\n1. Balance  2. Deposit  3. Withdraw  4. Mini statement  5. Exit");
            System.out.print("Choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Balance: Rs " + balance[idx]);
                    break;
                case 2:
                    deposit(idx);
                    break;
                case 3:
                    withdraw(idx);
                    break;
                case 4:
                    miniStatement(idx);
                    break;
                case 5:
                    System.out.println("Thank you, please take your card.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    // binary search on the sorted accNo array
    static int findAccount(int no) {
        int low = 0, high = accNo.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (accNo[mid] == no) return mid;
            else if (accNo[mid] < no) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    static void deposit(int idx) {
        System.out.print("Amount to deposit: ");
        int amt = sc.nextInt();
        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        balance[idx] += amt;
        history[idx].push("Deposit    +" + amt + "   bal: " + balance[idx]);
        System.out.println("Deposited. New balance: Rs " + balance[idx]);
    }

    static void withdraw(int idx) {
        System.out.print("Amount to withdraw: ");
        int amt = sc.nextInt();

        if (amt <= 0 || amt % 100 != 0) {
            System.out.println("Amount must be a positive multiple of 100.");
            return;
        }
        if (amt > LIMIT) {
            System.out.println("Limit per transaction is Rs " + LIMIT);
            return;
        }
        if (amt > balance[idx]) {
            System.out.println("Insufficient balance.");
            return;
        }

        balance[idx] -= amt;
        history[idx].push("Withdraw   -" + amt + "   bal: " + balance[idx]);

        // greedy: always take the biggest note that still fits
        System.out.println("Please collect your cash:");
        int left = amt;
        for (int n : notes) {
            int count = left / n;
            if (count > 0) {
                System.out.println("  Rs " + n + " x " + count);
                left = left % n;
            }
        }
        System.out.println("Remaining balance: Rs " + balance[idx]);
    }

    // pop from the history stack to print latest first, then push back
    static void miniStatement(int idx) {
        Stack<String> h = history[idx];
        if (h.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        Stack<String> temp = new Stack<>();
        System.out.println("Last transactions (latest first):");
        int shown = 0;
        while (!h.isEmpty()) {
            String t = h.pop();
            temp.push(t);
            if (shown < 5) {
                System.out.println("  " + t);
                shown++;
            }
        }
        // put everything back in the original order
        while (!temp.isEmpty()) {
            h.push(temp.pop());
        }
    }
}
