package com.timaimee.vpdemo.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.timaimee.vpdemo.R;

/**
 * 高性能版 可折叠日志卡片
 * 修复：滑动卡顿、SeekBar卡顿、嵌套ScrollView卡顿、事件拦截导致的掉帧
 */
public class CollapseCardLogView extends CardView {

    private TextView tvTitle;
    private TextView tvSubTitle;
    private ImageView ivArrow;
    private ImageView ivClearLog;
    private LinearLayout layoutTitleRoot;
    private ConstraintLayout layoutContentRootAnim;
    private LinearLayout layoutContentContainer;
    private ScrollView svTestInfo;
    private TextView tvTestInfo;
    private ConstraintLayout maskView; // 全屏半透明蒙版
    private boolean isFunctionEnabled = true;
    private final SpannableStringBuilder sb = new SpannableStringBuilder();

    private boolean isExpanded = false;
    private static final int ANIM_DURATION = 220;
    private ValueAnimator mHeightAnimator;

    private OnExpandStateChangeListener stateChangeListener;

    // 日志区域默认高度 200dp
    private int mLogScrollViewHeight = 200;

    public CollapseCardLogView(Context context) {
        this(context, null);
    }

    public CollapseCardLogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseCardLogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(true);
        View.inflate(context, R.layout.layout_collapse_log_card, this);

        tvTitle = findViewById(R.id.tvCollapseTitle);
        tvSubTitle = findViewById(R.id.tvCollapseSubTitle);
        ivArrow = findViewById(R.id.ivCollapseArrow);
        ivClearLog = findViewById(R.id.ivClearLog);
        layoutTitleRoot = findViewById(R.id.layoutTitleRoot);
        layoutContentRootAnim = findViewById(R.id.layoutContentRootAnim);
        layoutContentContainer = findViewById(R.id.layoutContentContainer);
        svTestInfo = findViewById(R.id.svInternalTestInfo);
        tvTestInfo = findViewById(R.id.tvInternalTestInfo);
        maskView = findViewById(R.id.maskView);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapseCardLogView);
        String title = ta.getString(R.styleable.CollapseCardLogView_collapseTitle);
        int titleColor = ta.getColor(R.styleable.CollapseCardLogView_collapseTitleColor, 0xFFE53935);
        float titleSize = ta.getDimension(R.styleable.CollapseCardLogView_collapseTitleSize, dp2px(15));
        boolean defaultExpand = ta.getBoolean(R.styleable.CollapseCardLogView_collapseDefaultExpand, false);
        int cardBg = ta.getColor(R.styleable.CollapseCardLogView_collapseCardBg, 0xFFFFFBEB);

        String subTitle = ta.getString(R.styleable.CollapseCardLogView_collapseSubTitle);
        int subTitleColor = ta.getColor(R.styleable.CollapseCardLogView_collapseSubTitleColor, 0xFF757575);
        float subTitleSize = ta.getDimension(R.styleable.CollapseCardLogView_collapseSubTitleSize, dp2px(12));

        mLogScrollViewHeight = ta.getDimensionPixelSize(R.styleable.CollapseCardLogView_collapseLogHeight, dp2px(200));

        ta.recycle();

        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);

        if (subTitle != null && !subTitle.isEmpty()) {
            tvSubTitle.setText(subTitle);
            tvSubTitle.setTextColor(subTitleColor);
            tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subTitleSize);
            tvSubTitle.setVisibility(VISIBLE);
        } else {
            tvSubTitle.setVisibility(GONE);
        }

        setCardBackgroundColor(cardBg);
        setRadius(dp2px(5));
        setCardElevation(dp2px(3));

        ViewGroup.LayoutParams layoutParams = svTestInfo.getLayoutParams();
        layoutParams.height = mLogScrollViewHeight;
        svTestInfo.setLayoutParams(layoutParams);

        layoutTitleRoot.setOnClickListener(v -> toggle());
        svTestInfo.setVerticalScrollBarEnabled(true);
        svTestInfo.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ivClearLog.setOnClickListener(view -> clearTestInfo());

        post(() -> {
            if (defaultExpand) {
                isExpanded = true;
                layoutContentRootAnim.setVisibility(VISIBLE);
                ViewGroup.LayoutParams p = layoutContentRootAnim.getLayoutParams();
                p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutContentRootAnim.setLayoutParams(p);
                ivArrow.setRotation(90);
            } else {
                isExpanded = false;
                layoutContentRootAnim.setVisibility(GONE);
                ViewGroup.LayoutParams p = layoutContentRootAnim.getLayoutParams();
                p.height = 0;
                layoutContentRootAnim.setLayoutParams(p);
                ivArrow.setRotation(0);
            }
        });
    }

    public void setFunctionTitle(String functionTitle) {
        tvTitle.setText(functionTitle);
    }

    /**
     * 设置功能是否可用
     */
    public void setFunctionEnabled(boolean enabled) {
        this.isFunctionEnabled = enabled;
        if (!enabled) {
            setSubTitle("⚠️ 当前功能暂不支持");
        }
        if (maskView != null) {
            maskView.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
    }

    public void setFunctionDisabled() {
        setFunctionEnabled(false);
    }

    public void setFunctionEnabled() {
        setFunctionEnabled(true);
    }

    public void setSubTitle(String subTitle) {
        if (subTitle == null || subTitle.isEmpty()) {
            tvSubTitle.setVisibility(GONE);
        } else {
            tvSubTitle.setText(subTitle);
            tvSubTitle.setVisibility(VISIBLE);
        }
    }

    public void setSubTitleColor(int color) {
        tvSubTitle.setTextColor(color);
    }

    public void setSubTitleSize(float spSize) {
        tvSubTitle.setTextSize(spSize);
    }

    public void setLogScrollViewHeight(int dpHeight) {
        this.mLogScrollViewHeight = dp2px(dpHeight);
        ViewGroup.LayoutParams params = svTestInfo.getLayoutParams();
        params.height = mLogScrollViewHeight;
        svTestInfo.setLayoutParams(params);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            ViewParent p = getParent();
            if (p != null) p.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void appendResult(String msg) {
        sb.append(msg).append("\n");
        tvTestInfo.setText(sb);
        svTestInfo.postDelayed(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN), 16);
    }

    public void appendRedLargeText(String msg) {
        SpannableString sp = new SpannableString(msg + "\n");
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#E53935")), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(15, true), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(sp);
        tvTestInfo.setText(sb);
        svTestInfo.postDelayed(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN), 16);
    }

    public void appendBlueMiddleText(String msg) {
        SpannableString sp = new SpannableString(msg + "\n");
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(14, true), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(sp);
        tvTestInfo.setText(sb);
        svTestInfo.postDelayed(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN), 16);
    }

    public void appendWhiteText(String msg) {
        SpannableString sp = new SpannableString(msg + "\n");
        sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(13, true), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(sp);
        tvTestInfo.setText(sb);
        svTestInfo.postDelayed(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN), 16);
    }

    public void appendOrangeText(String msg) {
        SpannableString sp = new SpannableString(msg + "\n");
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0F172A")), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(13, true), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), 0, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(sp);
        tvTestInfo.setText(sb);
        svTestInfo.postDelayed(() -> svTestInfo.fullScroll(ScrollView.FOCUS_DOWN), 16);
    }

    public void clearTestInfo() {
        sb.clear();
        tvTestInfo.setText("");
    }

    public void toggle() {
        if (isExpanded) collapse();
        else expand();
    }

    public void expand() {
        if (isExpanded) return;
        isExpanded = true;
        layoutContentRootAnim.setVisibility(VISIBLE);

        int widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        layoutContentRootAnim.measure(widthSpec, heightSpec);
        int target = layoutContentRootAnim.getMeasuredHeight();

        animateHeight(0, target);
        ivArrow.animate().rotation(90).setDuration(ANIM_DURATION).start();
    }

    public void collapse() {
        if (!isExpanded) return;
        isExpanded = false;
        int current = layoutContentRootAnim.getHeight();
        animateHeight(current, 0);
        ivArrow.animate().rotation(0).setDuration(ANIM_DURATION).start();
    }

    private void animateHeight(int from, int to) {
        if (mHeightAnimator != null && mHeightAnimator.isRunning()) {
            mHeightAnimator.cancel();
        }
        mHeightAnimator = ValueAnimator.ofInt(from, to);
        mHeightAnimator.setDuration(ANIM_DURATION);
        mHeightAnimator.setInterpolator(new DecelerateInterpolator());
        mHeightAnimator.addUpdateListener(anim -> {
            ViewGroup.LayoutParams p = layoutContentRootAnim.getLayoutParams();
            p.height = (int) anim.getAnimatedValue();
            layoutContentRootAnim.setLayoutParams(p);
        });
        mHeightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isExpanded) {
                    layoutContentRootAnim.setVisibility(GONE);
                } else {
                    ViewGroup.LayoutParams p = layoutContentRootAnim.getLayoutParams();
                    p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutContentRootAnim.setLayoutParams(p);
                }
                if (stateChangeListener != null) {
                    if (isExpanded) stateChangeListener.onExpanded(CollapseCardLogView.this);
                    else stateChangeListener.onCollapsed(CollapseCardLogView.this);
                }
            }
        });
        mHeightAnimator.start();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public interface OnExpandStateChangeListener {
        void onExpanded(CollapseCardLogView cardView);
        void onCollapsed(CollapseCardLogView cardView);
    }

    public void setOnExpandStateChangeListener(OnExpandStateChangeListener listener) {
        this.stateChangeListener = listener;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (layoutContentContainer == null) {
            super.addView(child, index, params);
        } else {
            layoutContentContainer.addView(child, index, params);
        }
    }
}