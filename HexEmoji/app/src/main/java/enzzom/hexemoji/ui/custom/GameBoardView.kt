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
import enzzom.hexemoji.utils.recyclerview.HexagonalSpanSizeLookup
import kotlin.math.abs

private const val MIN_SCALE_FACTOR = 0.5f
private const val MAX_SCALE_FACTOR = 1f

/**
 * A custom view representing a hexagonal game board with scrolling (pan) and scaling (zoom in/out) functionality.
 * This view requires a [GameBoardAdapter] to work properly, which should be set using the [setGameBoardAdapter] method.
 */
@SuppressLint("ClickableViewAccessibility")
class GameBoardView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val binding: ViewGameBoardBinding

    private val movementGestureDetector: GestureDetectorCompat
    private val scaleGestureDetector: ScaleGestureDetector

    private var boardMovementEnabled: Boolean = true
    private var boardLargerThanViewport: Boolean = false

    init {
        binding = ViewGameBoardBinding.inflate(LayoutInflater.from(context), this).apply {
            gameBoard.itemAnimator = null

            // Disabling extra scroll events because they will be treated solely by the 'gestureDetector'
            gameBoardVerticalScroll.setOnTouchListener { _, _ -> true }
            gameBoardHorizontalScroll.setOnTouchListener { _, _ -> true }

            root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val gameBoardPadding = resources.getDimensionPixelSize(R.dimen.game_board_padding)

                    // If the gameBoard is smaller than the screen, then there is no need for padding
                    if (gameBoard.width - (gameBoardPadding * 2) < root.width) {
                        gameBoard.setPadding(0, gameBoard.paddingTop, 0, gameBoard.paddingBottom)
                    } else {
                        boardLargerThanViewport = true
                    }

                    if (gameBoard.height - (gameBoardPadding * 2) < root.height) {
                        gameBoard.setPadding(gameBoard.paddingLeft, 0,gameBoard.paddingRight, 0)
                    } else {
                        boardLargerThanViewport = true
                    }

                    // Centering the gameBoard
                    gameBoardVerticalScroll.scrollTo(0, abs(gameBoard.height - root.height) / 2)
                    gameBoardHorizontalScroll.scrollTo(abs(gameBoard.width - root.width) / 2, 0)

                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        movementGestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
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

        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
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

    fun isBoardLargerThanViewport() = boardLargerThanViewport

    fun getCardViewForPosition(cardPosition: Int): EmojiCardView? {
        return binding.gameBoard.findViewHolderForAdapterPosition(cardPosition)?.let {
            (it as GameBoardAdapter.EmojiCardHolder).emojiCardView
        }
    }

    fun enableBoardMovement(enable: Boolean) {
        boardMovementEnabled = enable
    }

    fun enableCardInteraction(enable: Boolean) {
        binding.gameBoard.adapter?.let {
            (it as GameBoardAdapter).enableCardInteraction(enable)
        }
    }

    fun setGameBoardAdapter(gameBoardAdapter: GameBoardAdapter) {
        binding.gameBoard.apply {
            adapter = gameBoardAdapter

            val gridManager = GridLayoutManager(context, gameBoardAdapter.gridSpanCount, RecyclerView.VERTICAL, false)
            gridManager.spanSizeLookup = HexagonalSpanSizeLookup(gameBoardAdapter.gridSpanCount)

            layoutManager = gridManager
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (boardMovementEnabled) {
            movementGestureDetector.onTouchEvent(ev)
            scaleGestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }
}