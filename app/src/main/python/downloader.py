from __future__ import unicode_literals
import yt_dlp as youtube_dl
import os
import time
from threading import *
import sys

class downloader:
    def __init__(self,url, dl_format, directory, max_best_quality, log_path):
        self.url = url
        self.dl_format = dl_format
        self.directory = directory
        self.max_best_quality = max_best_quality
        self.log_path = log_path
        self.fail = False
        self.stop=False
        self.status = 'Stopped'
        self.progress = 0

    def getTime(self):
        time_stamp = time.localtime()
        return str(time_stamp.tm_mday)+"/"+str(time_stamp.tm_mon)+"/"+str(time_stamp.tm_year)+"::"+str(time_stamp.tm_hour)+":"+str(time_stamp.tm_min)+":"+str(time_stamp.tm_sec)

    def writeLog(self,message,logfile, filename = "/yt.log.txt"):
        message = self.getTime()+"="+message+"\n"

        if not os.path.exists(logfile):
            try:
                os.mkdir(logfile)
                with open(logfile+filename, 'w+') as lf:
                    lf.write(message)
            except Exception as e:
                print(e)
        else:
            try:
                with open(logfile+filename, "a") as lf:
                    lf.write(message)
            except Exception as e:
                print(e)

    def download(self):
        self.status='Preparing'
        self.process = Thread(target=self.downloadAction,args=(self.directory,self.dl_format, self.max_best_quality,self.url))

        if os.path.exists(self.log_path+"/yt.log.txt"):
            os.remove(self.log_path+"/yt.log.txt")
        try:
            self.process.start()
            return True
        except Exception as e:
            if "not a valid URL" in str(e):
                self.writeLog("ERROR :"+str(e), self.log_path)
                self.fail = True
                return False
            time.sleep(10)
            self.writeLog("PY WARNING : first exception :"+str(e)+", wait 10s and retry", self.log_path)
            self.fail = True
            return False

    def downloadAction(self,directory,dl_format,max_best_quality,url):

        ydl_opts = {}
        if directory[-1] != "/":
            directory+="/"
        if dl_format == "mp4":
            ydl_opts = {
                'format': 'best[height<='+max_best_quality+']',
                'outtmpl': directory+"%(title)s.%(ext)s",
                'progress_hooks': [self.my_hook],
            }
        elif dl_format == "m4a":
            ydl_opts = {
                'format': 'bestaudio[ext=m4a]',
                'outtmpl': directory+"%(title)s.%(ext)s",
                'progress_hooks': [self.my_hook],
            }
        try:
            with youtube_dl.YoutubeDL(ydl_opts) as ydl:
                ydl.download([url])
        except ValueError:
            self.fail = True
        except Exception as e:
            print("error ",str(e))
            time.sleep(5)
            try:
                with youtube_dl.YoutubeDL(ydl_opts) as ydl:
                    ydl.download([url])
            except Exception as e:
                print("error ",str(e))
                self.fail = True

    def get_video_info(self):
                    ydl_opts = {
                        'extract_flat': True,
                        'skip_download': True,
                    }
                    try:
                        with youtube_dl.YoutubeDL(ydl_opts) as ydl:
                            info = ydl.extract_info(self.url, download=False)
                            title = info.get('title')
                            thumbnail = info.get('thumbnail')
                            view_count = info.get('view_count')
                            like_count = info.get('like_count')
                            return {
                                'title': title,
                                'thumbnail': thumbnail,
                                'view_count': view_count,
                                'like_count': like_count
                            }
                    except Exception as e:
                        print("error ", str(e))
                        return None
#     def my_hook(self, d):
#             if d['status'] == 'downloading':
#                 self.progress = int(d['_percent_str'].strip('%'))
#                 self.status = d['_percent_str']
#                 if self.stop:
#                     self.status = 'Stopped'
#                     raise ValueError('Stopping process')
    def my_hook(self,d):
        if d['status'] == 'downloading':
            self.status=d['_percent_str']
            if self.stop==True:
                self.status='Stopped'
                raise ValueError('Stopping process')
    def isFail(self):
        return self.fail
    def state(self):
        return self.process.is_alive()
    def stop(self):
        self.stop=True


