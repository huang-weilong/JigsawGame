import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 *
 * @author huang-weilong
 */
public class Jigsaw extends JFrame implements ActionListener{
    private JPanel panel2;
    private JButton select;      //选择图片按钮
    private String filename;       //图片文件名
    private ImageIcon[] pic;       //存放小方格图片
    private ImageIcon last;             //存放最后一张小方格图片
    private JButton[][] btn;       //小方格控件
    private int m=3,n=3;        //设置为3*3的阵列
    private int step=0;         //步数
    private JLabel sum=new JLabel("步数：0步");     //实时步数标签
    private JLabel finish=new JLabel();     //完成游戏时提示标签
    
    void JigsawInit(){
        this.setTitle("拼图游戏");      //设置标题
        //网格布局
        GridLayout gridlayout1 = new GridLayout(3, 3);        //3行3列
        BorderLayout borderlayout1 = new BorderLayout();
        //边界布局
        BorderLayout borderlayout2 = new BorderLayout();
        JPanel panel1 = new JPanel(borderlayout1);       //面板1用于放置“步数”标签
        panel2=new JPanel(gridlayout1);         //面板2为游戏主界面
        //三个面板
        JPanel panel3 = new JPanel(borderlayout2);       //面板3放置选择图片按钮
        getContentPane().add(panel1,"North");   //将面板加入到内容窗格中
        getContentPane().add(panel2,"Center");
        getContentPane().add(panel3,"South");
        panel1.add(sum,"West");     //实时步数标签放置在面板1的West
        
        //设置选择图片按钮
        URL imgURL1;
        imgURL1=Jigsaw.class.getResource("select.jpg");      //获取“选择图片”的图片路径
        ImageIcon img1=new ImageIcon(imgURL1);      //创建图片图标
        select=new JButton(img1);           //创建选择图片按钮
        panel3.add(select,"Center");        //设置选择图片的位置
        select.addActionListener(this);     //添加事件监听器
        panel3.add(finish,"West");          //设置完成标签的位置
        
        setSize(500,600);       //设置窗口大小为500*650
        setVisible(true);       //显示窗口
        setResizable(false);    //窗口不能调节大小
        cutPic();       //调用cutPic()
    }
    //切割图片
    private void cutPic(){
        int w = 490 / m;        //每一小方格控件的宽
        //小方格的高度与宽度
        int h = 450 / n;        //每一小方格控件的高
        pic=new ImageIcon[m*n]; //分配空间大小
        if(btn==null)           //判断控件是否存在，不存在则分配新的空间
            btn=new JButton[m][n];  //分配控件实例化 
        //小方格的位置与大小
        for(int i=0;i<m*n;i++){
            int x=i/n,y=i%n;      //下标转换
            if(btn[x][y]==null)   //如果存在，就不需要重新实例化控件
                btn[x][y]=new JButton();      //实例化每个控件
            btn[x][y].addActionListener(this);    //添加事件监听器
            panel2.add(btn[x][y]);        //按钮控件加入到面板2中
        }
        int[] temp = randomArray(m * n);      //调用randarray使拼图随机排布
        Image[] t=splitImage(filename,m,n, w, h);//图片分割m*n个每个高度与宽度为w，h，按顺序分割
        for(int i=0;i<m*n-1;i++){
            pic[i]=new ImageIcon(t[i]); //图片转换image转为ImageIcon
        }
        last=new ImageIcon(t[8]);
        for(int i=0;i<m*n;i++){         //把图片放到方格内
            btn[i/n][i%n].setIcon(pic[temp[i]]);    //设置图标
        }
    }
    //图片方格随机产生一组随机序列数组
    private int[] randomArray(int n){
        int[] r = new int[n];
        for(int i=0;i<n;i++)        //初始化9个数字
            r[i]=i;
        for(int i=0;i<n;i++){
            int t;
            int a=(int)(Math.random()*n);   //产生随机数,使排列随机
            t=r[i];
            r[i]=r[a];
            r[a]=t;
        }
        return r;
    }
    // 图片分割方法
    private Image[] splitImage(String file, int rows, int cols, int w, int h){
        Image t = new ImageIcon(file).getImage();
        Image[] result = new Image[rows*cols];
        for (int i=0;i<result.length;i++){
            result[i]=createImage(w,h);
            Graphics g=result[i].getGraphics();
            g.translate((-i%cols)*w,(-i/cols)*h);
            g.drawImage(t, 0, 0, w*cols, h*rows, this);
        }
        return result;
    }
    //检查拼图是否完成
    private boolean check(){
        boolean b=true;
        for(int i=0;i<m*n;i++){       //逐一检查每块图片的位置是否排列正确
            int x1=i/n,y1=i%n;
            if (btn[x1][y1].getIcon() != pic[i]) {       //如果没有对上则b记为false
                b=false;
            }
        }
        return b;
    }
    //事件响应
    @Override
    public void actionPerformed(ActionEvent e){
        //当用户点击选择图片按钮时，弹出对话框，供用户选择游戏图片
        if(e.getSource()==select){
            FileDialog f=new FileDialog(this,"选择图片",FileDialog.LOAD);
            f.setVisible(true);
            filename=f.getDirectory()+f.getFile();      //文件路径+文件名
            finish.setVisible(false);
            step=0;
            sum.setText("步数："+step+"步");    //显示步数
            cutPic();
            return;
        }
        for(int i=0;i<m*n;i++){     //循环所有控件
            int x1=i/n,y1=i%n;      //下标转换
            if(e.getSource()==btn[x1][y1]){     //如果点击了小方格
                //向上移动
                if(x1>0&&btn[x1-1][y1].getIcon()==null){
                    btn[x1-1][y1].setIcon(btn[x1][y1].getIcon());   //获取当前图标给上一个控件为图标
                    btn[x1][y1].setIcon(null);      //当前图标设置为空
                    step++;     //步数加1
                }
                //向下移动
                if(x1<m-1&&btn[x1+1][y1].getIcon()==null){
                    btn[x1+1][y1].setIcon(btn[x1][y1].getIcon());   //获取当前图标给下一个控件为图标
                    btn[x1][y1].setIcon(null);      //当前图标设置为空
                    step++;     //步数加1
                }
                //向左移动
                if(y1>0&&btn[x1][y1-1].getIcon()==null){
                    btn[x1][y1-1].setIcon(btn[x1][y1].getIcon());   //获取当前图标给左一个控件为图标
                    btn[x1][y1].setIcon(null);      //当前图标设置为空
                    step++;     //步数加1
                }
                //向右移动
                if(y1<n-1&&btn[x1][y1+1].getIcon()==null){
                    btn[x1][y1+1].setIcon(btn[x1][y1].getIcon());   //获取当前图标给右一个控件为图标
                    btn[x1][y1].setIcon(null);      //当前图标设置为空
                    step++;     //步数加1
                }
                sum.setText("步数："+step+"步");    //设置实时步数的更新
            }
        }
        //如果完成游戏
        if(check()){
            finish.setVisible(true);        //显示完成游戏的标签
            finish.setFont(new Font("宋体", Font.PLAIN,35));      //设置文本字体及大小
            finish.setText("<html><p>完成游戏</p><p>共使用"+step+"步</p></html>");//设置文本
            btn[2][2].setIcon(last);
            for(int j=0;j<m*n;j++){
                int x2=j/n,y2=j%n;      //下标转换
                btn[x2][y2].removeActionListener(this);     //完成以后清除所有的小方格的事件监听
            }
        }
    }
}