using System;
using System.IO;
using System.Windows.Forms;
using OpenCvSharp;
using OpenCvSharp.Extensions;
using System.Threading;
using System.Drawing;
using System.Drawing.Imaging;

namespace Face_acqusition
{
    public partial class CaptureLayout : Form
    {
        private VideoCapture vc;
        private VideoWriter vw = new VideoWriter();

        private string name;
        private const string path = @"C:\sclab\";

        public CaptureLayout(string name)
        {
            InitializeComponent();
            this.CenterToScreen();

            this.name = name;
            DirectoryInfo di = new DirectoryInfo(path + name);

            if (di.Exists == false)
            {
                di.Create();
            }
        }

        private void CaptureLayout_Load(object sender, EventArgs e)
        {
            timer.Enabled = false;

            try
            {
                vc.Dispose();
            }
            catch { }

            vc = new VideoCapture(0);
            timer.Enabled = true;
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            Mat frame = new Mat();
            vc.Read(frame);

            if (frame.Empty()) { return; }
            else
            {
                try
                {
                    this.Invoke((Action)(() =>
                    {
                        vw.Write(frame);
                        live_box.Image = DrawRect(BitmapConverter.ToBitmap(frame));
                    }), null);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("timer_Tick() error");
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);
                }
            }
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
            catch (Exception) { }

            return img;
        }

        public Bitmap LoadBitmap(string path)
        {
            if (File.Exists(path))
            {
                // open file in read only mode
                using (FileStream stream = new FileStream(path, FileMode.Open, FileAccess.Read))
                // get a binary reader for the file stream
                using (BinaryReader reader = new BinaryReader(stream))
                {
                    // copy the content of the file into a memory stream
                    var memoryStream = new MemoryStream(reader.ReadBytes((int)stream.Length));
                    // make a new Bitmap object the owner of the MemoryStream
                    return new Bitmap(memoryStream);
                }
            }
            else
            {
                MessageBox.Show("Error Loading File.", "Error!", MessageBoxButtons.OK);
                return null;
            }
        }

        private void capture_btn_Click(object sender, EventArgs e)
        {
            string cur_name = DateTime.Now.ToString("yyyyMMdd-HHmmss");
            string cur_file = path + name + @"\" + cur_name + ".png";

            live_box.Image.Save(cur_file, ImageFormat.Png);

            temp_box.Image = LoadBitmap(cur_file);

            string cur_msg = "Do you want to save temporary photos?\n Save when you click 'Yes',\n Don't save when you click 'No'.";
            if (MessageBox.Show(cur_msg, "", MessageBoxButtons.YesNo) == DialogResult.Yes)
            {
                MessageBox.Show("Save !");
            }
            else
            {
                if (File.Exists(cur_file))
                {
                    File.Delete(cur_file);
                }
                MessageBox.Show("Delete !");
            }
        }

        private void CaptureLayout_FormClosing(object sender, FormClosingEventArgs e)
        {

        }
    }
}