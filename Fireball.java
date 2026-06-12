public class Fireball {
    int size = 50;
    int damage = 50;
    public Fireball(int strength) {
        if (strength < 25) {
            size = 25;
            damage = 25;
        }else if (strength < 75) {
            size = 50;
            damage = 50;
        }else {
            size = 75;
            damage = 80;
        }
    }
    
}
