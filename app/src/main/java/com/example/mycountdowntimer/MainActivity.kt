package com.example.mycountdowntimer

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.mycountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var soundResId = 0  // サウンドファイルのサウンドIDを保持するプロパティを宣言

    inner class MyCountDownTimer(millisInFuture: Long,   // ネスト(入れ子)のクラスにinnerをつけることによって、ネストクラス内から外部クラスのメンバ(bindingなど)にアクセスできるようになる
                                  countDownInterval: Long
    ): CountDownTimer(millisInFuture, countDownInterval){  // CountDownTimerクラスを継承して、MyCountDownTimerクラスを生成
        // millisInFuture:タイマーの残り時間をミリ秒で指定,  countDownInterval:onTickメソッドを実行する瞬間をミリ秒で指定

        var isRunning = false   //カウントダウン中か停止中かを表すフラグ

        override fun onTick(millisUntilFinished: Long){  //onTick(): 指定した間隔で実行したい処理を記述
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            binding.timerText.text = "%1d:%2$02d".format(minute, second)
            // 「%1」は引数リストの1番目(minute)を、「d」は整数で表示を、また、「%2$02d」は引数リストの2番目(second)を2桁の整数(02d)で表すことを示している
        }

        override fun onFinish(){    // onFinish():　タイマー終了時に呼び出される
            binding.timerText.text = "0:00"
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.timerText.text = "3:00"
        var timer = MyCountDownTimer(3*60*1000,100)  //MyCountDownTimerをインスタンス化
        binding.playStop.setOnClickListener{
            timer.isRunning = when(timer.isRunning){
                true ->{  //カウントダウン中だった場合
                    timer.cancel()  //カウントダウンを停止
                    binding.playStop.setImageResource(   //setImageResource: 画像を設定
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    false
                }
                false ->{  //停止中だった場合
                    timer.start()   //カウントダウンを開始
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_stop_24
                    )
                    true
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        soundPool =
            SoundPool.Builder().run{   // run{}: スコープ関数の一つであり、ブロック{ }内の処理を実行し、最後の処理を返す関数
                val audioAttributes = AudioAttributes.Builder().run{ //オーディオストリームに関する属性を指定して、後でSoundPoolに返すため、AudioAttributesクラスのインスタンスを生成
                    setUsage(AudioAttributes.USAGE_ALARM)  //オーディオの使用目的を説明
                    build()  //buildでAudioAttributesのインスタンスを生成
                }
                setMaxStreams(1)  //同時再生可能なストリームの最大数を設定
                setAudioAttributes(audioAttributes)    //AudioAttributesを設定
                build()  //SoundPoolクラスのインスタンス化
            }
        soundResId = soundPool.load(this, R.raw.bellsound, 1)  //load: リソースからサウンドファイルを読み込む
                                     //上記のthisはsoundPool, 戻り値はサウンドID
    }

    override fun onPause(){
        super.onPause()
        soundPool.release()  //release: メモリ開放
    }
}