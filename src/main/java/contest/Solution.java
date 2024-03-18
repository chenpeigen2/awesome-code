package contest;

public class Solution {
    public void heita(int[] monsters, int e, int r) {
        int cntE = 0;
        int cntR = 0;

        boolean[] divided = new boolean[monsters.length];
        int[] cpMonsters = new int[monsters.length];
        System.arraycopy(monsters, 0, cpMonsters, 0, monsters.length);

        int attachR = 0;

        while (shouldAttach(monsters)) {
            if (attachR > 0) {
                attachR--;
                for (int i = 0; i < divided.length; i++) {
                    if (!divided[i] && monsters[i] > cpMonsters[i] / 2 && monsters[i] - r <= cpMonsters[i] / 2) {
                        divided[i] = true;
                        attachR++;
                    }
                    monsters[i] -= r;
                }
                cntR++;
            } else {
                for (int i = 0; i < divided.length; i++) {
                    if (!divided[i] && monsters[i] > cpMonsters[i] / 2 && monsters[i] - e <= cpMonsters[i] / 2) {
                        divided[i] = true;
                        attachR++;
                    }
                    monsters[i] -= e;
                }
                cntE++;
            }
        }

        System.out.println(cntE + " " + cntR);

    }

    private boolean shouldAttach(int[] monsters) {
        for (int monster : monsters) {
            if (monster > 0) return true;
        }
        return false;
    }
}
