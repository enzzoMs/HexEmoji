package enzzom.hexemoji.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import enzzom.hexemoji.R
import enzzom.hexemoji.databinding.ViewGameBoardBinding
import enzzom.hexemoji.ui.fragments.game.adapters.GameBoardAdapter
import enzzom.hexemoji.utils.recyclerview.HexagonalSpanSizeLookup
import kotlin.math.abs

private const val MIN_SCALE_FACTOR = 0.5f
private const val MAX_SCALE_FACTOR = 1f

/**
 * A custom view representing a hexagonal game board with scrolling (pan) and scaling (zoom in/out) functionality.
 */
@SuppressLint("ClickableViewAccessibility")
class GameBoardView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val binding: ViewGameBoardBinding
    private val gestureDetector: GestureDetectorCompat
    private val scaleDetector: ScaleGestureDetector
    private var boardMovementEnabled: Boolean = true
    private var boardLargerThanScreen: Boolean = false

    init {
        val inflater = LayoutInflater.from(context)

        binding = ViewGameBoardBinding.inflate(inflater, this)

        binding.apply {
            // Disabling extra scroll events because they will be treated solely by the 'gestureDetector'
            gameBoardVerticalScroll.setOnTouchListener { _, _ -> true }
            gameBoardHorizontalScroll.setOnTouchListener { _, _ -> true }

            root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.apply {
                        val gameBoardPadding = resources.getDimensionPixelSize(R.dimen.game_board_padding)

                        // If the gameBoard is smaller than the screen, then there is no need for padding
                        if (gameBoard.width - (gameBoardPadding * 2) < root.width) {
                            gameBoard.setPadding(0, gameBoard.paddingTop, 0, gameBoard.paddingBottom)
                        } else {
                            boardLargerThanScreen = true
                        }

                        if (gameBoard.height - (gameBoardPadding * 2) < root.height) {
                            gameBoard.setPadding(gameBoard.paddingLeft, 0,gameBoard.paddingRight, 0)
                        } else {
                            boardLargerThanScreen = true
                        }

                        // Centering the gameBoard
                        gameBoardVerticalScroll.scrollTo(0, abs(gameBoard.height - root.height) / 2)
                        gameBoardHorizontalScroll.scrollTo(abs(gameBoard.width - root.width) / 2, 0)
                    }
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            private var currentX: Float = 0f
            private var currentY: Float = 0f

            override fun onDown(e: MotionEvent): Boolean {
                currentX = e.x
                currentY = e.y
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                val scrollDistanceX = (currentX - e2.x).toInt()
                val scrollDistanceY = (currentY - e2.y).toInt()

                currentX = e2.x
                currentY = e2.y

                binding.apply {
                    gameBoardHorizontalScroll.scrollBy(scrollDistanceX, 0)
                    gameBoardVerticalScroll.scrollBy(0, scrollDistanceY)
                }
                return true
            }
        })

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private var scaleFactor = 1f

            override fun onScale(p0: ScaleGestureDetector): Boolean {
                binding.apply {
                    val newScaleFactor = scaleFactor * p0.scaleFactor

                    if (newScaleFactor in MIN_SCALE_FACTOR .. MAX_SCALE_FACTOR) {
                        scaleFactor *= p0.scaleFactor

                        gameBoard.scaleX = scaleFactor
                        gameBoard.scaleY = scaleFactor
                    }
                }
                return true
            }
        })
    }

    fun setGameBoardAdapter(gameBoardAdapter: GameBoardAdapter) {
        binding.gameBoard.apply {
            adapter = gameBoardAdapter

            val gridManager = GridLayoutManager(context, gameBoardAdapter.gridSpanCount, RecyclerView.VERTICAL, false)
            gridManager.spanSizeLookup = HexagonalSpanSizeLookup(gameBoardAdapter.gridSpanCount)

            layoutManager = gridManager
        }
    }

    fun enableBoardMovement(enable: Boolean) {
        boardMovementEnabled = enable
    }

    fun isBoardLargerThanScreen() = boardLargerThanScreen

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        if (boardMovementEnabled) {
            gestureDetector.onTouchEvent(event)
            scaleDetector.onTouchEvent(event)
        }
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (boardMovementEnabled) {
            gestureDetector.onTouchEvent(ev)
            scaleDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }
}