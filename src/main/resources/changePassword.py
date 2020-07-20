# 批量修改linux用户名
import logging
import os
import random
import string
import time

import paramiko
from paramiko import AuthenticationException

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(filename)s [line:%(lineno)d] %(levelname)s:%(message)s',
                    filename='change.log')

sender = "wenhao.zhang@neoway.com"
passwd = "Nw202013#@"
receive_email = ['jacky.song@neoway.com', 'jianying.xu@neoway.com', 'zheng.wenbin@neoway.com',
                 'liang.fen@neoway.com', 'tuo.yang@neoway.com', 'chao.wang@neoway.com', 'hongbo.dai@neoway.com', 'wenhao.zhang@neoway.com']


class SSHD():
    def __init__(self, host_name, user_name, pass_word, number, open_file):
        self.HostName = host_name
        self.UserName = user_name
        self.PassWord = pass_word
        self.Number = int(number)
        self.f = open_file

    # 修改密码，并记录文件
    def change(self):
        try:
            chars = string.ascii_letters + string.digits
            result_pass_wd = "".join(random.choice(chars) for _ in range(self.Number))
            s = paramiko.SSHClient()
            s.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            s.connect(hostname=self.HostName, username=self.UserName, password=self.PassWord)
            stdin, stdout, stderr = s.exec_command('echo %s | passwd --stdin root' % result_pass_wd)
            change_into = stdout.read().decode('utf-8')
            if 'successfully.' in change_into:
                logging.info("%s,修改密码成功,密码为:%s" % (self.HostName, result_pass_wd))
                f.writelines(self.HostName + '\t' + result_pass_wd + '\n')
            else:
                logging.error("%s,原密码:%s ===> 新密码:%s 修改失败,执行命令失败！" % (self.HostName, self.PassWord, result_pass_wd))
                f.writelines(self.HostName + '\t' + self.PassWord + '\n')
        except (AuthenticationException, Exception) as e:
            logging.error("%s,原密码:%s ===> 新密码:%s 修改失败，无法登录服务器！" % (self.HostName, self.PassWord, result_pass_wd), e)
            f.writelines(self.HostName + '\t' + self.PassWord + '\n')
        finally:
            s.close()


# 读取原来的密码
def get_passwd(old_hosts):
    with open(old_hosts) as file:
        next(file)
        while 1:
            line = file.readline()
            if not line or len(line.strip().split()) != 2:
                break
            hostname, password = line.strip().split()
            yield hostname, 'root', password


'''
函数说明：Send_email_text() 函数实现发送带有附件的邮件，可以群发，附件格式包括：xlsx,pdf,txt,jpg,mp3等
参数说明：
    1. subject：邮件主题
    2. content：邮件正文
    3. filepath：附件的地址, 输入格式为["","",...]
    4. receive_email：收件人地址, 输入格式为["","",...]
'''


def send_email_text(subject, content, filepath, receive_email):
    import smtplib
    from email.mime.multipart import MIMEMultipart
    from email.mime.text import MIMEText
    from email.mime.application import MIMEApplication
    receivers = receive_email  # 收件人邮箱
    msgRoot = MIMEMultipart()
    msgRoot['Subject'] = subject
    msgRoot['From'] = sender
    if len(receivers) > 1:
        msgRoot['To'] = ','.join(receivers)  # 群发邮件
    else:
        msgRoot['To'] = receivers[0]

    part = MIMEText(content)
    msgRoot.attach(part)

    ##添加附件部分
    for path in filepath:
        if ".jpg" in path:
            # jpg类型附件
            jpg_name = path.split("\\")[-1]
            part = MIMEApplication(open(path, 'rb').read())
            part.add_header('Content-Disposition', 'attachment', filename=jpg_name)
            msgRoot.attach(part)
        if ".pdf" in path:
            # pdf类型附件
            pdf_name = path.split("\\")[-1]
            part = MIMEApplication(open(path, 'rb').read())
            part.add_header('Content-Disposition', 'attachment', filename=pdf_name)
            msgRoot.attach(part)
        if ".xlsx" in path:
            # xlsx类型附件
            xlsx_name = path.split("\\")[-1]
            part = MIMEApplication(open(path, 'rb').read())
            part.add_header('Content-Disposition', 'attachment', filename=xlsx_name)
            msgRoot.attach(part)
        if ".txt" in path:
            # txt类型附件
            txt_name = path.split("\\")[-1]
            part = MIMEApplication(open(path, 'rb').read())
            part.add_header('Content-Disposition', 'attachment', filename=txt_name)
            msgRoot.attach(part)
        if ".mp3" in path:
            # mp3类型附件
            mp3_name = path.split("\\")[-1]
            part = MIMEApplication(open(path, 'rb').read())
            part.add_header('Content-Disposition', 'attachment', filename=mp3_name)
            msgRoot.attach(part)
    try:
        s = smtplib.SMTP()
        s.connect("192.168.2.61")
        s.login(sender, passwd)
        s.sendmail(sender, receivers, msgRoot.as_string())
        logging.info("发送邮件成功！")
    except (smtplib.SMTPException, Exception) as e:
        logging.error("发送邮件失败！", e)
    finally:
        s.quit()


def get_old_file():
    files = []
    for file in os.listdir('./'):
        if file.split(".")[-1] == 'txt' and '-' in file:
            files.append(file)
    files.sort(reverse=True)
    return files[0]


if __name__ == '__main__':
    Host = []
    g = get_passwd(get_old_file())
    for i in g:
        Host.append(i)
    file_name = time.strftime("%Y-%m-%d", time.localtime()) + '.txt'
    with open(file_name, mode='w') as f:
        f.writelines(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()) + '\n')
        for HostName, UserName, PassWord in Host:
            ssh = SSHD(HostName, UserName, PassWord, 20, f)
            ssh.change()

    subject = "修改密码通知"
    content = "微软云定期修改密码,新密码见附件"
    file_path = [file_name]
    if os.path.exists(file_name):
        send_email_text(subject, content, file_path, receive_email)
