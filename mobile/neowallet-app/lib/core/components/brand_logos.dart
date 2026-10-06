import 'package:flutter/material.dart';

import '../theme/design_tokens.dart';

const String _verticalLogo = 'assets/branding/NeoWallet-HR-Vertical.png';
const String _horizontalLogo = 'assets/branding/NeoWallet-HR-Horizontal.png';

class NeoWalletSplashLogo extends StatelessWidget {
  final double width;

  const NeoWalletSplashLogo({
    super.key,
    this.width = 220,
  });

  @override
  Widget build(BuildContext context) {
    return Image.asset(
      _verticalLogo,
      width: width,
      fit: BoxFit.contain,
      semanticLabel: 'NeoWallet logo',
    );
  }
}

class NeoWalletHorizontalLogo extends StatelessWidget {
  final double? width;
  final double? height;

  const NeoWalletHorizontalLogo({
    super.key,
    this.width,
    this.height = 36,
  });

  @override
  Widget build(BuildContext context) {
    return Image.asset(
      _horizontalLogo,
      width: width,
      height: height,
      fit: BoxFit.contain,
      semanticLabel: 'NeoWallet logo',
    );
  }
}

class BrandAppBar extends StatelessWidget implements PreferredSizeWidget {
  final Widget? title;
  final List<Widget>? actions;
  final PreferredSizeWidget? bottom;
  final bool? centerTitle;

  const BrandAppBar({
    super.key,
    this.title,
    this.actions,
    this.bottom,
    this.centerTitle,
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(
      leading: const Padding(
        padding: EdgeInsets.all(DesignTokens.spaceSm),
        child: NeoWalletHorizontalLogo(),
      ),
      automaticallyImplyLeading: false,
      title: title,
      actions: actions,
      bottom: bottom,
      centerTitle: centerTitle ?? false,
    );
  }

  @override
  Size get preferredSize => Size.fromHeight(
        kToolbarHeight + (bottom?.preferredSize.height ?? 0),
      );
}
