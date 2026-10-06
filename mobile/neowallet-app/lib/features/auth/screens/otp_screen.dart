import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/components/brand_logos.dart';
import '../../../core/di/providers.dart';

class OtpScreen extends ConsumerStatefulWidget {
  final String? email;

  const OtpScreen({super.key, this.email});

  @override
  ConsumerState<OtpScreen> createState() => _OtpScreenState();
}

class _OtpScreenState extends ConsumerState<OtpScreen> {
  final _otp = TextEditingController();
  final _form = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    if (widget.email != null) {
      Future.microtask(() {
        ref.read(authProvider.notifier).requestOtp(email: widget.email, purpose: 'REGISTRATION');
      });
    }
  }

  @override
  void dispose() {
    _otp.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(authProvider);
    final email = state.email ?? widget.email ?? '';

    ref.listen(authProvider, (prev, next) {
      if (next.isOtpVerified && !next.isLoading) {
        context.go('/login', extra: email);
      }
    });

    return Scaffold(
      appBar: AppBar(automaticallyImplyLeading: false),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Form(
            key: _form,
            child: ListView(
              children: [
                const NeoWalletHorizontalLogo(width: 200),
                const SizedBox(height: 8),
                Text(
                  'Verify your email',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
                const SizedBox(height: 24),
                Text(
                  'Enter the 6-digit code sent to ${email.isEmpty ? 'your email' : email}',
                  style: Theme.of(context).textTheme.bodyLarge,
                ),
                const SizedBox(height: 24),
                TextFormField(
                  controller: _otp,
                  keyboardType: TextInputType.number,
                  maxLength: 6,
                  decoration: const InputDecoration(
                    labelText: 'Verification code',
                    border: OutlineInputBorder(),
                    counterText: '',
                  ),
                  validator: (v) => v == null || v.length != 6 ? 'Enter the 6-digit code' : null,
                ),
                const SizedBox(height: 16),
                if (state.error != null)
                  Text(
                    state.error!,
                    style: TextStyle(color: Theme.of(context).colorScheme.error),
                    textAlign: TextAlign.center,
                  ),
                const SizedBox(height: 8),
                ElevatedButton(
                  onPressed: state.isLoading ? null : _submit,
                  child: state.isLoading
                      ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
                      : const Text('Verify'),
                ),
                const SizedBox(height: 8),
                TextButton(
                  onPressed: state.isLoading ? null : _resend,
                  child: const Text('Resend code'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _submit() async {
    if (!_form.currentState!.validate()) return;
    FocusScope.of(context).unfocus();
    await ref.read(authProvider.notifier).verifyOtp(code: _otp.text.trim());
  }

  Future<void> _resend() async {
    await ref.read(authProvider.notifier).resendOtp();
  }
}
