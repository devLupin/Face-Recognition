using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace WindowsFormsApp1
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();

            picturebox1.Image = DrawRect(new Bitmap(@"C:\Users\devLupin\Desktop\캡처.PNG"));    // 사각형 그려질 거
            pictureBox2.Image = new Bitmap(@"C:\Users\devLupin\Desktop\캡처.PNG");    // 원본
        }

        private Image DrawRect(Image img)
        {
            const int sx = 125, sy = 10, ex = 600, ey = 450;
            const int sx2 = 150, sy2 = 50, ex2 = 550, ey2 = 430;

            try
            {
                Rectangle raBig = new Rectangle(sx, sy, ex - sx, ey - sy);
                Rectangle raSmall = new Rectangle(sx2, sy2, ex2 - sx2, ey2 - sy2);

                using (Graphics grp = Graphics.FromImage(img))
                {
                    Brush bigBrush = new SolidBrush(Color.FromArgb(16, 0, 0, 255));
                    grp.FillRectangle(bigBrush, raBig);

                    Pen pSmall = new Pen(Color.FromArgb(128, 0, 255, 0), 4);
                    pSmall.DashStyle = System.Drawing.Drawing2D.DashStyle.Solid;
                    grp.DrawRectangle(pSmall, raSmall);
                }

                return img;
            }
            catch (Exception) {  }

            return img;
        }
    }
}
