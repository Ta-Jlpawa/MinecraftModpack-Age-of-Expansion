ServerEvents.recipes(event => {
    // 黑洞只允许作为内部注册项存在，不提供任何可制作配方。
    event.remove({ output: 'oritech:black_hole_block' });
});
