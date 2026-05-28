package com.timaimee.vpdemo.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.timaimee.vpdemo.R;

/**
 * 支持XML直接嵌套内容的 可折叠卡片（带展开/收起监听）
 */
public class CollapseCardView extends CardView {

    private TextView tvTitle;
    private ImageView ivArrow;
    private LinearLayout layoutTitleRoot;
    private LinearLayout layoutContentContainer;

    private boolean isExpanded = false;
    private static final int ANIM_DURATION = 250;

    // 展开/收起 监听
    private OnExpandStateChangeListener stateChangeListener;

    public CollapseCardView(Context context) {
        this(context, null);
    }

    public CollapseCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // 加载内部结构
        View.inflate(context, R.layout.layout_collapse_card, this);
        tvTitle = findViewById(R.id.tvCollapseTitle);
        ivArrow = findViewById(R.id.ivCollapseArrow);
        layoutTitleRoot = findViewById(R.id.layoutTitleRoot);
        layoutContentContainer = findViewById(R.id.layoutContentContainer);

        // 读取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapseCardView);
        String title = ta.getString(R.styleable.CollapseCardView_collapseTitle);
        int titleColor = ta.getColor(R.styleable.CollapseCardView_collapseTitleColor, 0xFFE53935);
        float titleSize = ta.getDimension(R.styleable.CollapseCardView_collapseTitleSize, 15);
        boolean defaultExpand = ta.getBoolean(R.styleable.CollapseCardView_collapseDefaultExpand, false);
        int cardBg = ta.getColor(R.styleable.CollapseCardView_collapseCardBg, 0xFFFFFBEB);
        ta.recycle();

        // 设置样式 —— 【修复字体大小问题】
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        setCardBackgroundColor(cardBg);
        setRadius(dp2px(5));
        setCardElevation(dp2px(10));

        // 跑马灯生效
        tvTitle.setSelected(true);

        // 点击切换
        layoutTitleRoot.setOnClickListener(v -> toggle());

        // 默认展开/关闭
        post(() -> {
            if (defaultExpand) expand();
            else collapse();
        });
    }

    /**
     * 设置展开/收起状态监听
     */
    public void setOnExpandStateChangeListener(OnExpandStateChangeListener listener) {
        this.stateChangeListener = listener;
    }

    /**
     * 展开/收起 监听接口
     */
    public interface OnExpandStateChangeListener {
        void onExpanded(CollapseCardView cardView);  // 展开完成
        void onCollapsed(CollapseCardView cardView); // 收起完成
    }

    /**
     * 关键：把XML里写的子View 全部添加到内容容器
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (layoutContentContainer == null) {
            super.addView(child, index, params);
        } else {
            layoutContentContainer.addView(child, index, params);
        }
    }

    public void toggle() {
        if (isExpanded) collapse();
        else expand();
    }

    public void expand() {
        if (isExpanded) return;
        isExpanded = true;
        layoutContentContainer.setVisibility(VISIBLE);

        layoutContentContainer.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int target = layoutContentContainer.getMeasuredHeight();
        ValueAnimator anim = ValueAnimator.ofInt(0, target);
        anim.addUpdateListener(a -> {
            ViewGroup.LayoutParams p = layoutContentContainer.getLayoutParams();
            p.height = (int) a.getAnimatedValue();
            layoutContentContainer.setLayoutParams(p);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 展开完成 回调
                if (stateChangeListener != null) {
                    stateChangeListener.onExpanded(CollapseCardView.this);
                }
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.start();
        ivArrow.animate().rotation(90).setDuration(ANIM_DURATION).start();
    }

    public void collapse() {
        if (!isExpanded) return;
        isExpanded = false;
        int initH = layoutContentContainer.getMeasuredHeight();
        ValueAnimator anim = ValueAnimator.ofInt(initH, 0);
        anim.addUpdateListener(a -> {
            ViewGroup.LayoutParams p = layoutContentContainer.getLayoutParams();
            p.height = (int) a.getAnimatedValue();
            layoutContentContainer.setLayoutParams(p);
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layoutContentContainer.setVisibility(GONE);
                ViewGroup.LayoutParams p = layoutContentContainer.getLayoutParams();
                p.height = LayoutParams.WRAP_CONTENT;
                layoutContentContainer.setLayoutParams(p);

                // 收起完成 回调
                if (stateChangeListener != null) {
                    stateChangeListener.onCollapsed(CollapseCardView.this);
                }
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.start();
        ivArrow.animate().rotation(0).setDuration(ANIM_DURATION).start();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}