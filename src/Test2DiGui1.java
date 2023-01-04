public class Test2DiGui1 {
    //无递归头和递归体（会报错）
    public static void main(String[] args) {
        Test2DiGui1 test = new Test2DiGui1();
        test.add();
    }
    public void add(){
        add();
    }
}
